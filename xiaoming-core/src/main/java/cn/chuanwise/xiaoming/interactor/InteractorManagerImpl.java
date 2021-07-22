package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
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

        // 设置群标记
        if (user instanceof GroupXiaomingUser) {
            user.setProperty("group", ((GroupXiaomingUser) user).getGroupCodeString());
        }

        for (Interactor interactor : coreInteractors) {
            // 如果是指令交互器，且成功交互了
            if (interactorClass.isAssignableFrom(interactor.getClass()) &&
                    interactor.willInteract(user) &&
                    interactor.interact(user, message)) {
                interacted = true;
            }
        }

        // 如果本群没有启动小明就不管了
        if (user instanceof GroupXiaomingUser && !((GroupXiaomingUser) user).getContact().hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag())) {
            return interacted;
        }

        for (Map.Entry<XiaomingPlugin, Set<Interactor>> entry : pluginInteractors.entrySet()) {
            final XiaomingPlugin plugin = entry.getKey();
            final Set<Interactor> interactors = entry.getValue();

            // 用户没屏蔽，不在群里或者本群也没屏蔽
            boolean usePlugin = !user.hasTag("plugin.block." + plugin.getName()) &&
                    (!(user instanceof GroupXiaomingUser) ||
                            !getXiaomingBot().getGroupRecordManager().hasTag(((GroupXiaomingUser) user).getGroupCode(), "plugin.block." + plugin.getName()));

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
    public boolean onInput(XiaomingUser user, Message message) throws Exception {
        return onInput(user, message, Interactor.class);
    }

    @Override
    public void register(Interactor interactor, XiaomingPlugin plugin) {
        if (Objects.nonNull(plugin)) {
            getOrPutInteractors(plugin).add(interactor);
            interactor.setPlugin(plugin);
        } else {
            coreInteractors.add(interactor);
        }
        interactor.setXiaomingBot(getXiaomingBot());
        interactor.initialize();
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