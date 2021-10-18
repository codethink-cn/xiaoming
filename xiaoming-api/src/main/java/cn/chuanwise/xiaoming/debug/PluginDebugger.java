package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.launcher.XiaomingLauncher;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import org.slf4j.Logger;

import java.util.List;

public interface PluginDebugger {
    XiaomingBot getXiaomingBot();

    void run() throws Exception;

    XiaomingLauncher getLauncher();

    List<PluginHandler> getPluginHandlers();

    Logger getLogger();
}
