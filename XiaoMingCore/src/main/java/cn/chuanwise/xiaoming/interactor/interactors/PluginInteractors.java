package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.CollectionUtil;
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
    @Required("plugin.list")
    public void listPlugins(XiaomingUser user) {
        final Map<String, Plugin> plugins = getXiaomingBot().getPluginManager().getPlugins();
        final Map<Plugin.Status, Set<Plugin>> status = new HashMap<>();

        for (Plugin plugin : plugins.values()) {
            MapUtil.getOrPutSupply(status, plugin.getStatus(), HashSet::new).add(plugin);
        }
        if (plugins.isEmpty()) {
            user.sendMessage("没有启动任何插件哦");
        } else {
            user.sendMessage("插件状态：\n" +
                    CollectionUtil.toIndexString(status.entrySet(), x -> x.getKey() + "：" + CollectionUtil.toString(x.getValue(), Plugin::getCompleteName)));
        }
    }

    @Filter(CommandWords.PLUGIN + " {插件名}")
    @Required("plugin.look")
    public void lookPlugin(XiaomingUser user, @FilterParameter("插件名") Plugin plugin) {
        if (Objects.isNull(plugin)) {
            user.sendError("想偷看内核机密，这思想很危险嗷！");
        } else {
            user.sendMessage("插件名：" + plugin.getName() + "\n" +
                    "版本：" + plugin.getVersion() + "\n" +
                    "消息交互器：" + xiaomingBot.getInteractorManager().getInteractors(plugin).size() + " 个\n" +
                    "事件监听器：" + xiaomingBot.getEventManager().getListeners(plugin).size() + " 个");
        }
    }
}
