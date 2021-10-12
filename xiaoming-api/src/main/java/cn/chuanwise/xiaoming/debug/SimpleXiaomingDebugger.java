package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.launcher.XiaomingLauncher;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
public class SimpleXiaomingDebugger implements XiaomingDebugger {
    final XiaomingLauncher launcher;
    final List<PluginHandler> pluginHandlers;

    final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public XiaomingBot getXiaomingBot() {
        return getLauncher().getXiaomingBot();
    }

    @Override
    public void run() throws Exception {
        launch();
    }

    protected void launch() throws Exception {
        launcher.launch();
        if (launcher.start()) {
            final PluginManager pluginManager = getXiaomingBot().getPluginManager();
            pluginManager.addPlugins(pluginHandlers);

            for (PluginHandler pluginHandler : pluginHandlers) {
                if (pluginManager.loadPlugin(pluginHandler) && pluginManager.enablePlugin(pluginHandler)) {
                    logger.info("成功载入调试插件：" + pluginHandler.getName());
                }
            }
        } else {
            logger.error("启动调试失败");
        }
    }
}
