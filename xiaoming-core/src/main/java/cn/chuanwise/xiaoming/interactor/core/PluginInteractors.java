package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.util.MapUtil;
import cn.chuanwise.xiaoming.annotation.*;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.*;

public class PluginInteractors extends SimpleInteractors {
    PluginManager pluginManager;

    @Override
    public void onRegister() {
        pluginManager = xiaomingBot.getPluginManager();
    }

    @Filter(CommandWords.PLUGIN)
    @Permission("plugin.list")
    public void onListPlugins(XiaomingUser user) {
        final Map<String, Plugin> plugins = getXiaomingBot().getPluginManager().getPlugins();
        final Map<Plugin.Status, Set<Plugin>> status = new HashMap<>();

        for (Plugin plugin : plugins.values()) {
            MapUtil.getOrPutSupply(status, plugin.getStatus(), HashSet::new).add(plugin);
        }
        if (plugins.isEmpty()) {
            user.sendMessage("{lang.noAnyLoadedPlugin}");
        } else {
            user.sendMessage("{lang.pluginStatus}", status);
        }
    }

    @Filter(CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.look")
    public void onLookPlugin(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        user.sendMessage("{lang.pluginDetail}", plugin);
    }

    @Filter(CommandWords.LOAD + CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.load")
    public void onLoadPlugin(XiaomingUser user, @FilterParameter("插件名") PluginHandler handler) {
        if (handler.isLoaded()) {
            user.sendError("{lang.pluginAlreadyLoaded}", handler.getPlugin());
            return;
        }
        final String pluginName = handler.getName();
        if (getXiaomingBot().getPluginManager().loadPlugin(pluginName)) {
            user.sendMessage("{lang.pluginLoaded}", pluginName);
        } else {
            user.sendError("{lang.failToLoadPlugin}", pluginName);
        }
    }

    @Filter(CommandWords.UNLOAD + CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.unload")
    public void onUnloadPlugin(XiaomingUser user, @FilterParameter("插件名") PluginHandler handler) {
        if (!handler.isLoaded()) {
            user.sendError("{lang.pluginHadNotLoad}", handler.getName());
            return;
        }
        final String pluginName = handler.getName();
        if (getXiaomingBot().getPluginManager().unloadPlugin(pluginName)) {
            user.sendMessage("{lang.pluginUnloaded}", pluginName);
        } else {
            user.sendError("{lang.failToUnloadPlugin}", pluginName);
        }
    }

    @Filter(CommandWords.ENABLE + CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.enable")
    public void onEnablePlugin(XiaomingUser user, @FilterParameter("插件名") PluginHandler handler) {
        final Plugin plugin = handler.getPlugin();
        if (handler.isEnabled()) {
            user.sendError("{lang.pluginAlreadyEnabled}", plugin);
        } else if (pluginManager.enablePlugin(handler)) {
            user.sendMessage("{lang.pluginEnabled}", plugin);
        } else {
            user.sendError("{lang.failToEnablePlugin}", plugin);
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.disable")
    public void onDisablePlugin(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        if (!plugin.getHandler().isEnabled()) {
            user.sendError("{lang.pluginHadNotEnabled}", plugin);
        } else if (pluginManager.disablePlugin(plugin)) {
            user.sendMessage("{lang.pluginDisabled}", plugin);
        } else {
            user.sendError("{lang.failToDisablePlugin}", plugin);
        }
    }

    @Filter(CommandWords.RELOAD + CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.reload")
    public void onReloadPlugin(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        final PluginHandler handler = plugin.getHandler();
        if (pluginManager.reloadPlugin(handler)) {
            user.sendMessage("{lang.pluginReloaded}", handler);
        } else {
            user.sendError("{lang.failToReloadPlugin}", handler);
        }
    }

    @Filter(CommandWords.REENABLE + CommandWords.PLUGIN + " {插件名}")
    @Permission("plugin.reenable")
    public void onReenablePlugin(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        if (!plugin.getHandler().isEnabled()) {
            user.sendError("{lang.pluginHadNotEnabled}", plugin);
        } else if (pluginManager.reenablePlugin(plugin)) {
            user.sendMessage("{lang.pluginReenabled}", plugin);
        } else {
            user.sendError("{lang.failToReenablePlugin}", plugin);
        }
    }
}
