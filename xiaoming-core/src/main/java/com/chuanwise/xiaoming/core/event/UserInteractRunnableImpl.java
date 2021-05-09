package com.chuanwise.xiaoming.core.event;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.InteractorResponseEvent;
import com.chuanwise.xiaoming.api.event.PluginResponseEvent;
import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.event.UserInteractor;
import com.chuanwise.xiaoming.api.exception.InteactorTimeoutException;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.interactor.MessageWaiter;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.account.AccountEventImpl;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import kotlinx.coroutines.TimeoutCancellationException;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * 负责和一个用户交互的线程
 * @author Chuanwise
 */
@Data
public class UserInteractRunnableImpl extends HostXiaomingObjectImpl implements UserInteractRunnable {
    XiaomingUser user;

    public UserInteractRunnableImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    /**
     * 消息等待器
     */
    MessageWaiter messageWaiter;

    /**
     * 正在与用户交互的交互器
     */
    Interactor interactor;

    @Override
    public String getNextInput(long waitTime, Function<Void, Void> onTimeout) {
        if (Objects.nonNull(messageWaiter)) {
            throw new XiaomingRuntimeException("message waiter already set!");
        }
        final long endTime = waitTime + System.currentTimeMillis();
        messageWaiter = new MessageWaiter(endTime);
        try {
            synchronized (messageWaiter) {
                messageWaiter.wait(waitTime);
            }
        } catch (InterruptedException ignored) {
        }

        // 如果消息为空，可能是超时，也可能是本次交互被中止（从群聊穿越到私聊，自动取消群聊上次的上下文交互）
        if (Objects.isNull(messageWaiter) || Objects.isNull(messageWaiter.getValue())) {
            if (System.currentTimeMillis() > endTime) {
                onTimeout.apply(null);
            } else {
                throw new InteactorTimeoutException();
            }
        }

        String value = messageWaiter.getValue();
        messageWaiter = null;
        return value;
    }

    @Override
    public boolean interact() throws Exception {
        final XiaomingBot xiaomingBot = getXiaomingBot();
        boolean interacted = false;

        // 尝试内核交互器
        final InteractorManager interactorManager = xiaomingBot.getInteractorManager();
        for (Interactor coreInteractor : interactorManager.getCoreInteractors()) {
            if (coreInteractor.willInteract(user)) {
                interactor = coreInteractor;
                coreInteractor.interact(user);
                interactor = null;

                // 发出交互器响应事件
                interacted = true;
                getXiaomingBot().getEventListenerManager().callLater(new InteractorResponseEvent(coreInteractor, null));
            }
        }
        for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : interactorManager.getPluginInteractors().entrySet()) {
            final XiaomingPlugin plugin = entry.getKey();
            // 只有该用户有权限与该插件交互，且该插件没有被用户屏蔽，且本群没有屏蔽本插件时才能交互
            boolean usePlugin = user.hasPermission("enable." + plugin.getName()) && !user.isBlockPlugin(plugin.getName());
            if (user instanceof GroupXiaomingUser) {
                final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().fromCode(((GroupXiaomingUser) user).getGroupNumber());
                usePlugin = usePlugin && !responseGroup.isBlockPlugin(plugin.getName());
            }

            if (usePlugin) {
                for (Interactor pluginInteractor : entry.getValue()) {
                    if (pluginInteractor.willInteract(user)) {
                        interactor = pluginInteractor;
                        pluginInteractor.interact(user);
                        interactor = null;

                        // 发出插件交互器响应事件
                        interacted = true;
                        getXiaomingBot().getEventListenerManager().callLater(new InteractorResponseEvent(pluginInteractor, plugin));
                    }
                }
            }
        }

        // 先尝试匹配指令处理器
        if (xiaomingBot.getCommandManager().onCommand(user)) {
            // onCommand 会自动发出事件，无须再次发出
            interacted = true;
        }

        // 再尝试把消息给所有的插件本体
        final PluginManager pluginManager = getXiaomingBot().getPluginManager();
        for (XiaomingPlugin plugin : pluginManager.getEnabledPlugins()) {
            // 只有该用户有权限与该插件交互，且该插件没有被用户屏蔽，且本群没有屏蔽本插件时才能交互
            boolean usePlugin = user.hasPermission("enable." + plugin.getName()) && !user.isBlockPlugin(plugin.getName());
            if (user instanceof GroupXiaomingUser) {
                final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().fromCode(((GroupXiaomingUser) user).getGroupNumber());
                usePlugin = usePlugin && !responseGroup.isBlockPlugin(plugin.getName());
            }

            if (usePlugin && plugin.onMessage(user)) {
                // 发出插件交互器响应事件
                interacted = true;
                getXiaomingBot().getEventListenerManager().callLater(new PluginResponseEvent(plugin));
            }
        }
        return interacted;
    }

    @Override
    public void run() {
        while (!getXiaomingBot().isStop()) {
            // 等待另一个线程唤醒当前进程，以与其交互
            try {
                final UserInteractor userInteractor = user.getUserInteractor();
                synchronized (userInteractor) {
                    userInteractor.wait();
                }
            } catch (InterruptedException ignored) {
            }
            // 如果不在继续执行则退出
            if (getXiaomingBot().isStop()) {
                return;
            }

            try {
                final boolean isInteracted = interact();

                if (isInteracted) {
                    // 如果交互成功，记录当前事件
                    final Account account = user.getOrPutAccount();
                    if (user instanceof GroupXiaomingUser) {
                        account.addCommand(new AccountEventImpl(((GroupXiaomingUser) user).getGroupNumber(), user.getMessage()));
                    } else {
                        account.addCommand(new AccountEventImpl(user.getMessage()));
                    }
                    getLog().info(user.getName() + " 执行指令：" + user.getMessage());
                    getXiaomingBot().getRegularPreserveManager().readySave(account);
                } else {
                    // 交互失败，清除最近的几次输入
                    user.clearRecentInputs();

                    // 如果是私聊，还要告诉小明听不懂
                    if (!(user instanceof GroupXiaomingUser)) {
                        user.sendError("小明不知道你的意思，赶快使用 #帮助 查看如何使用小明吧 {}", getXiaomingBot().getWordManager().get("happy"));
                    }
                }
            } catch (TimeoutCancellationException ignored) {
            } catch (InteactorTimeoutException exception) {
                interactor = null;
                messageWaiter = null;
            } catch (Exception exception) {
                user.sendError("小明遇到了一个问题，这个问题已经上报了，期待更好的小明吧 {}", getXiaomingBot().getWordManager().get("happy"));
                getLog().error("与用户交互时出现异常", exception);
                // 把异常写入错误报告
                if (user instanceof GroupXiaomingUser) {
                    getXiaomingBot().getErrorMessageManager().addGroupThrowableMessage(((GroupXiaomingUser) user), exception);
                } else {
                    getXiaomingBot().getErrorMessageManager().addThrowableMessage(user, exception);
                }
            }
        }
    }
}