package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.EventListener;
import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PluginInteractor extends CommandInteractorImpl {
    final PluginManager pluginManager;

    static final String UNLOAD = "(卸载|unload)";
    static final String LOAD = "(加载|load)";
    static final String ENABLE = "(启动|enable)";
    static final String DISABLE = "(关闭|disable)";
    static final String RELOAD = "(重载|reload)";
    static final String REENABLE = "(重启|reenable)";
    static final String FLUSH = "(刷新|flush)";

    public PluginInteractor(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
        pluginManager = xiaomingBot.getPluginManager();
    }

    @Filter(CommandWords.PLUGIN_REGEX)
    @RequirePermission("plugin.list")
    public void onListPlugins(XiaomingUser user) {
        final Set<XiaomingPlugin> enabledPlugins = pluginManager.getEnabledPlugins();
        StringBuilder builder = new StringBuilder();

        builder.append("启动的插件：");
        if (enabledPlugins.isEmpty()) {
            builder.append("（无）");
        } else {
            for (XiaomingPlugin plugin : enabledPlugins) {
                builder.append("\n").append(plugin.getName());
            }
        }
        builder.append("\n");

        // 获得仅加载了的插件
        final Set<XiaomingPlugin> loadedPlugins = pluginManager.getLoadedPlugins();
        final Set<XiaomingPlugin> loadOnlyPlugins = new HashSet<>();
        for (XiaomingPlugin plugin : loadedPlugins) {
            if (!enabledPlugins.contains(plugin)) {
                loadOnlyPlugins.add(plugin);
            }
        }

        builder.append("仅加载的插件：");
        if (loadOnlyPlugins.isEmpty()) {
            builder.append("（无）");
        } else {
            for (XiaomingPlugin plugin : loadOnlyPlugins) {
                builder.append("\n").append(plugin.getName());
            }
        }
        builder.append("\n");

        final Set<String> unloadedPluginName = new HashSet<>();
        for (String pluginName : pluginManager.getExistingPlugins().keySet()) {
            if (!pluginManager.isLoaded(pluginName)) {
                unloadedPluginName.add(pluginName);
            }
        }

        builder.append("未加载的插件：");
        if (unloadedPluginName.isEmpty()) {
            builder.append("（无）");
        } else {
            for (String name : unloadedPluginName) {
                builder.append("\n").append(name);
            }
        }
        user.sendMessage(builder.toString());
    }

    @Filter(FLUSH + CommandWords.PLUGIN_REGEX)
    @RequirePermission("plugin.flush")
    public void onFlushPlugins(XiaomingUser user) {
        pluginManager.flushPluginMap(user);
        user.sendMessage("插件列表刷新完成");
    }

    @Filter(CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.look")
    public void onLookPlugins(XiaomingUser user, @FilterParameter("name") String pluginName) {
        final XiaomingPlugin plugin = pluginManager.getLoadedPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            StringBuilder builder = new StringBuilder();
            builder.append("插件：").append(plugin.getCompleteName());
            builder.append("\n");

            final Set<Interactor> interactors = getXiaomingBot().getInteractorManager().getInteractors(plugin);
            builder.append("交互器：");
            if (Objects.isNull(interactors) || interactors.isEmpty()) {
                builder.append("（无）");
            } else {
                for (Interactor interactor : interactors) {
                    builder.append("\n").append(interactor.getClass().getName());
                }
            }
            builder.append("\n");

            final Set<EventListener> pluginListeners = getXiaomingBot().getEventManager().getPluginListeners(plugin);
            builder.append("监听器：");
            if (Objects.isNull(pluginListeners) || pluginListeners.isEmpty()) {
                builder.append("（无）");
            } else {
                for (EventListener listener : pluginListeners) {
                    builder.append("\n").append(listener.getClass().getName());
                }
            }
            user.sendMessage(builder.toString());
        } else if (Objects.nonNull(pluginManager.getLoadedPlugin(pluginName))) {
            user.sendMessage("插件{}已经加载了，但还没有启动", pluginName);
        } else if (Objects.nonNull(pluginManager.getPluginProperty(pluginName))) {
            user.sendMessage("插件{}还没有被加载", pluginName);
        } else {
            user.sendMessage("找不到插件{}", pluginName);
        }
    }

    @Filter(UNLOAD + CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.unload")
    public void onUnloadPlugin(XiaomingUser user, @FilterParameter("name") String pluginName) {
        pluginManager.unloadPlugin(user, pluginName);
    }

    @Filter(LOAD + CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.load")
    public void onLoadPlugin(XiaomingUser user, @FilterParameter("name") String pluginName) {
        pluginManager.loadPlugin(user, pluginName);
    }

    @Filter(ENABLE + CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.enable")
    public void onEnablePlugin(XiaomingUser user, @FilterParameter("name") String pluginName) {
        pluginManager.enablePlugin(user, pluginName);
    }

    @Filter(DISABLE + CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.disable")
    public void onDisablePlugin(XiaomingUser user, @FilterParameter("name") String pluginName) {
        pluginManager.disablePlugin(user, pluginName);
    }

    @Filter(RELOAD + CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.reload")
    public void onReloadPlugin(XiaomingUser user, @FilterParameter("name") String pluginName) {
        pluginManager.reloadPlugin(user, pluginName);
    }

    @Filter(REENABLE + CommandWords.PLUGIN_REGEX + " {name}")
    @RequirePermission("plugin.reenable")
    public void onReenablePlugin(XiaomingUser user, @FilterParameter("name") String pluginName) {
        pluginManager.reenablePlugin(user, pluginName);
    }
}
