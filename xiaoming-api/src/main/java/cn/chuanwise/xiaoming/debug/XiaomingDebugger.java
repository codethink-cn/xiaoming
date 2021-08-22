package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.xiaoming.bot.XiaomingBot;

public interface XiaomingDebugger {
    XiaomingBot getXiaomingBot();

    void debug() throws Exception;

    void run() throws Exception;

    cn.chuanwise.xiaoming.launcher.XiaomingLauncher getLauncher();

    java.util.List<cn.chuanwise.xiaoming.plugin.PluginHandler> getPluginHandlers();

    org.slf4j.Logger getLogger();
}
