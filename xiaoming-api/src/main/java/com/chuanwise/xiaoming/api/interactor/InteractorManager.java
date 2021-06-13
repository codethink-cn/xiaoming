package com.chuanwise.xiaoming.api.interactor;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Set;

public interface InteractorManager extends ModuleObject {
    /**
     * 和所有的指令交互器交互
     * @param user 用户
     * @return 是否交互成功
     * @throws Exception 交互期间抛出的异常
     */
    boolean onCommand(XiaomingUser user, Message message) throws Exception;

    boolean onMessage(XiaomingUser user, Message message) throws Exception;

    boolean onInput(XiaomingUser user, Message message) throws Exception;

    boolean onInput(XiaomingUser user, Message message, Class<? extends Interactor> interactorClass) throws Exception;

    /**
     * 注册交互器
     * @param interactor 交互器
     * @param plugin 注册方，如果是内核则为 {@code null}
     */
    void register(Interactor interactor, XiaomingPlugin plugin);

    /**
     * 获得某插件注册的所有交互器
     * @param plugin 插件
     * @return 该插件注册的所有交互器
     */
    Set<Interactor> getInteractors(XiaomingPlugin plugin);

    /**
     * 获得或新建一个插件注册的交互器集
     * @param plugin 插件
     * @return 该插件注册的所有交互器
     */
    Set<Interactor> getOrPutInteractors(XiaomingPlugin plugin);

    /**
     * 拒绝内核形式注册
     */
    void denyCoreRegister();

    Set<Interactor> getCoreInteractors();

    java.util.Map<XiaomingPlugin, Set<Interactor>> getPluginInteractors();
}
