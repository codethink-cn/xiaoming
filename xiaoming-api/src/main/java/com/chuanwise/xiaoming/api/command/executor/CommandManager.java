package com.chuanwise.xiaoming.api.command.executor;

import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Map;
import java.util.Set;

/**
 * 指令处理器管理器
 */
public interface CommandManager extends HostXiaomingObject {
    /**
     * 处理一个指令
     * @param user 指令发送者
     * @return 是否成功交互
     * @throws Exception 交互时出现的异常
     */
    boolean onCommand(XiaomingUser user) throws Exception;

    /**
     * 获得一个插件注册的所有指令处理器
     * @param plugin 注册方
     * @return 其注册的所有指令处理器。如果该插件没有注册过任何指令，则返回 {@code null}
     */
    Set<CommandExecutor> getPluginCommandExecutors(XiaomingPlugin plugin);

    /**
     * 获得一个插件注册的所有指令处理器。如果找不到，则创建一个。
     * @param plugin 注册方
     * @return 其注册的所有指令处理器。
     */
    Set<CommandExecutor> getOrPutCommandExecutors(XiaomingPlugin plugin);

    /**
     * 注册指令处理器
     * @param executor 指令处理器
     * @param plugin 处理器注册方。如果是 {@code null}，则注册为内核态。
     */
    void register(CommandExecutor executor, XiaomingPlugin plugin);

    /**
     * 禁止继续注册内核指令处理器，将其设置为只读。
     */
    void denyCoreRegister();

    Set<CommandExecutor> getCoreCommandExecutors();

    Map<XiaomingPlugin, Set<CommandExecutor>> getPluginCommandExecutors();
}
