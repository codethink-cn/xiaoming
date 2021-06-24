package com.chuanwise.xiaoming.core.interactor;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.interactor.command.CommandInteractor;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.interactor.message.MessageInteractor;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import lombok.Getter;

import java.util.*;

/**
 * 交互器管理器
 * @author Chuanwise
 */
@Getter
public class InteractorManagerImpl extends ModuleObjectImpl implements InteractorManager {
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
    public boolean onInput(XiaomingUser user, Message message, Class<? extends Interactor> interactorClass) throws Exception {
        // 先和内核交互器交互
        boolean interacted = false;

        for (Interactor interactor : coreInteractors) {
            // 如果是指令交互器，且成功交互了
            if (interactorClass.isAssignableFrom(interactor.getClass()) &&
                    interactor.willInteract(user) &&
                    interactor.interact(user, message)) {
                interacted = true;
            }
        }
        // 如果在群里，但是本群没有启动小明，就退出
        if (user instanceof GroupXiaomingUser) {
            final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().forCode(((GroupXiaomingUser) user).getGroupCode());
            if (Objects.isNull(responseGroup) || !responseGroup.hasTag("enable")) {
                return interacted;
            }
        }

        for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
            final XiaomingPlugin plugin = entry.getKey();
            final Set<Interactor> interactors = entry.getValue();

            // 用户没屏蔽，不在群里或者本群也没屏蔽
            boolean usePlugin = !user.isBlockPlugin(plugin.getName()) &&
                    (!(user instanceof GroupXiaomingUser) || !getXiaomingBot().getResponseGroupManager().forCode(((GroupXiaomingUser) user).getGroupCode()).isBlockPlugin(plugin.getName()));

            if (usePlugin) {
                for (Interactor interactor : interactors) {
                    if (interactorClass.isAssignableFrom(interactor.getClass()) &&
                            interactor.willInteract(user) &&
                            interactor.interact(user, message)) {
                        interacted = true;
                    }
                }
            }
        }
        return interacted;
    }

    @Override
    public boolean onCommand(XiaomingUser user, Message message) throws Exception {
        return onInput(user, message, CommandInteractor.class);
    }

    @Override
    public boolean onMessage(XiaomingUser user, Message message) throws Exception {
        return onInput(user, message, MessageInteractor.class);
    }

    @Override
    public boolean onInput(XiaomingUser user, Message message) throws Exception {
        return onInput(user, message, Interactor.class);
    }

    @Override
    public void register(Interactor interactor, XiaomingPlugin plugin) {
        if (Objects.nonNull(plugin)) {
            getOrPutInteractors(plugin).add(interactor);
            getLog().info("正在载入插件 " + plugin.getCompleteName() + " 交互器 {} 中的交互方法", interactor.getClass().getName());
        } else {
            coreInteractors.add(interactor);
            getLog().info("正在载入内核交互器 {} 中的交互方法", interactor.getClass().getName());
        }
        interactor.setXiaomingBot(getXiaomingBot());

        interactor.initialize();
        final Set<InteractorMethodDetail> methodDetails = interactor.getMethodDetails();
        if (methodDetails.isEmpty()) {
            getLog().info("没有载入任何交互方法");
        } else {
            getLog().info("成功载入 " + methodDetails.size() + " 个交互方法：\n" + CollectionUtils.getIndexSummary(methodDetails, detail -> {
                        return Arrays.toString(detail.getUsageStrings());
                    }, "", "", "、"));
        }
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