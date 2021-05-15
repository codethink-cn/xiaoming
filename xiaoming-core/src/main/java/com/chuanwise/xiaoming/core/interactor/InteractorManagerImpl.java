package com.chuanwise.xiaoming.core.interactor;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.InteractorResponseEvent;
import com.chuanwise.xiaoming.api.event.PluginResponseEvent;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Group;

import java.util.*;

/**
 * 交互器管理器
 * @author Chuanwise
 */
@Getter
public class InteractorManagerImpl extends HostObjectImpl implements InteractorManager {
    Set<Interactor> coreInteractors = new HashSet<>();

    Map<XiaomingPlugin, Set<Interactor>> pluginInteractors = new HashMap<>();

    public InteractorManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    /**
     * 以指令形式和用户交互
     * @param user
     * @param interactorClass 交互器父类
     * @return 是否交互成功
     * @exception Exception 交互期间抛出的异常
     */
    @Override
    public boolean onInput(XiaomingUser user, Class<? extends Interactor> interactorClass) throws Exception {
        // 先和内核交互器交互
        boolean interacted = false;

        for (Interactor interactor : coreInteractors) {
            // 如果是指令交互器，且成功交互了
            if (interactorClass.isAssignableFrom(interactor.getClass()) &&
                    interactor.willInteract(user) &&
                    interactor.interact(user)) {
                interacted = true;
            }
        }
        // 如果在群里，但是本群没有启动小明，就退出
        if (user.inGroup()) {
            final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().fromCode(user.getGroup().getId());
            if (Objects.isNull(responseGroup) || !responseGroup.hasTag("enable")) {
                return interacted;
            }
        }

        for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
            final XiaomingPlugin plugin = entry.getKey();
            final Set<Interactor> interactors = entry.getValue();

            // 用户没屏蔽，不在群里或者本群也没屏蔽
            boolean usePlugin = !user.isBlockPlugin(plugin.getName()) &&
                    (!user.inGroup() || !getXiaomingBot().getResponseGroupManager().fromCode(user.getGroup().getId()).isBlockPlugin(plugin.getName()));

            if (usePlugin) {
                if (plugin.onMessage(user)) {
                    // 增加调用统计次数
                    getXiaomingBot().getStatistician().increaseCallCounter();
                    getXiaomingBot().getEventListenerManager().callLater(new PluginResponseEvent(plugin, user));
                }
                for (Interactor interactor : interactors) {
                    if (interactorClass.isAssignableFrom(interactor.getClass()) &&
                            interactor.willInteract(user) &&
                            interactor.interact(user)) {
                        interacted = true;
                    }
                }
            }
        }
        return interacted;
    }

    @Override
    public boolean onCommand(XiaomingUser user) throws Exception {
        return onInput(user, CommandInteractor.class);
    }

    @Override
    public boolean onMessage(XiaomingUser user) throws Exception {
        return onInput(user, MessageInteractorImpl.class);
    }

    @Override
    public boolean onInput(XiaomingUser user) throws Exception {
        return onInput(user, Interactor.class);
    }

    @Override
    public void register(Interactor interactor, XiaomingPlugin plugin) {
        if (Objects.nonNull(plugin)) {
            getOrPutInteractors(plugin).add(interactor);
        } else {
            coreInteractors.add(interactor);
        }
        interactor.setXiaomingBot(getXiaomingBot());
        interactor.initialize();
        interactor.setPlugin(plugin);
    }

    @Override
    public Set<Interactor> getInteractors(XiaomingPlugin plugin) {
        return pluginInteractors.get(plugin);
    }

    @Override
    public Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin) {
        Set<Interactor> interactors = getInteractors(plugin);
        if (Objects.isNull(interactors)) {
            interactors = new HashSet<>();
            pluginInteractors.put(plugin, interactors);
        }
        return interactors;
    }

    @Override
    public void denyCoreRegister() {
        coreInteractors = Collections.unmodifiableSet(coreInteractors);
    }
}