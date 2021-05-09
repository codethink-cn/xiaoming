package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.command.executor.CommandExecutor;
import com.chuanwise.xiaoming.api.command.executor.CommandManager;
import com.chuanwise.xiaoming.api.event.CommandResponseEvent;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 指令管理器
 * @author Chuanwise
 */
@Getter
public class CommandManagerImpl extends HostXiaomingObjectImpl implements CommandManager {
    /**
     * 内核指令处理器
     */
    Set<CommandExecutor> coreCommandExecutors = new CopyOnWriteArraySet<>();

    /**
     * 插件指令处理器
     */
    Map<XiaomingPlugin, Set<CommandExecutor>> pluginCommandExecutors = new ConcurrentHashMap<>();

    public CommandManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public boolean onCommand(XiaomingUser user) throws Exception {
        boolean executed = false;
        // 内核不能屏蔽，所以不做权限验证
        for (CommandExecutor executor : coreCommandExecutors) {
            if (executor.onCommand(user)) {
                // 发出响应事件
                executed = true;
                getXiaomingBot().getEventListenerManager().callLater(new CommandResponseEvent(executor, null));
            }
        }
        for (Map.Entry<XiaomingPlugin, Set<CommandExecutor>> entry : pluginCommandExecutors.entrySet()) {
            final XiaomingPlugin plugin = entry.getKey();

            // 只有该用户有权限与该插件交互，且该插件没有被用户屏蔽，且本群没有屏蔽本插件时才能交互
            boolean usePlugin = user.hasPermission("enable." + plugin.getName()) && !user.isBlockPlugin(plugin.getName());
            if (user instanceof GroupXiaomingUser) {
                final ResponseGroup responseGroup = getXiaomingBot().getResponseGroupManager().fromCode(((GroupXiaomingUser) user).getGroupNumber());
                usePlugin = usePlugin && !responseGroup.isBlockPlugin(plugin.getName());
            }

            if (usePlugin) {
                for (CommandExecutor executor : entry.getValue()) {
                    if (executor.onCommand(user)) {
                        // 发出响应事件
                        executed = true;
                        getXiaomingBot().getEventListenerManager().callLater(new CommandResponseEvent(executor, plugin));
                    }
                }
            }
        }
        return executed;
    }

    @Override
    public Set<CommandExecutor> getPluginCommandExecutors(final XiaomingPlugin plugin) {
        return pluginCommandExecutors.get(plugin);
    }

    @Override
    public Set<CommandExecutor> getOrPutCommandExecutors(final XiaomingPlugin plugin) {
        Set<CommandExecutor> result = getPluginCommandExecutors(plugin);
        if (Objects.isNull(result)) {
            final HashSet<CommandExecutor> set = new HashSet<>();
            pluginCommandExecutors.put(plugin, set);
            return set;
        } else {
            return result;
        }
    }

    @Override
    public void register(CommandExecutor executor, XiaomingPlugin plugin) {
        if (Objects.isNull(plugin)) {
            getLog().info("注册来内核指令处理器：{}", executor.getClass().getName());
            coreCommandExecutors.add(executor);
        } else {
            getLog().info("注册来自 {} 插件的指令处理器：{}", plugin.getCompleteName(), executor.getClass().getName());
            executor.reloadSubcommandExecutor(plugin.getLogger());
            getOrPutCommandExecutors(plugin).add(executor);
        }
        executor.reloadSubcommandExecutor(getLog());
        executor.setXiaomingBot(getXiaomingBot());
    }

    @Override
    public void denyCoreRegister() {
        coreCommandExecutors = Collections.unmodifiableSet(coreCommandExecutors);
    }
}