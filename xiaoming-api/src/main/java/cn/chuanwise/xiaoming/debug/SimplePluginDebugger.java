package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.launcher.XiaomingLauncher;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import lombok.Data;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

@Data
public class SimplePluginDebugger implements PluginDebugger {
    final XiaomingLauncher launcher;
    final List<PluginHandler> pluginHandlers;

    final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public XiaomingBot getXiaomingBot() {
        return getLauncher().getXiaomingBot();
    }

    @Override
    public void run() throws Exception {
        // 加载配置文件
        final Charset defaultCharset = Charset.defaultCharset();
        final InputStream loggerPropertyStream = new ByteBufferBackedInputStream(ByteBuffer.wrap(("log4j.rootLogger = INFO, console, file\n" +
                "\n" +
                "log4j.appender.console=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.console.target=System.out\n" +
                "log4j.appender.console.layout=cn.chuanwise.xiaoming.log.ColorPatternLayout\n" +
                "log4j.appender.console.encoding=UTF-8\n" +
                "\n" +
                "log4j.appender.file=org.apache.log4j.DailyRollingFileAppender\n" +
                "log4j.appender.file.file=/debugger-log.log\n" +
                "log4j.appender.file.encoding=UTF-8\n" +
                "log4j.appender.file.append=true\n" +
                "log4j.appender.file.layout=cn.chuanwise.xiaoming.log.NocolorPatternLayout\n" +
                "log4j.appender.file.DatePattern='.'yyyy-MM-dd'.log'").getBytes()));

        PropertyConfigurator.configure(loggerPropertyStream);
        getLogger().info("日志设置配置完成");
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
