package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 插件主体对象
 * @author Chuanwise
 */
public interface PluginObject extends XiaomingObject {
    XiaomingPlugin getPlugin();

    void setPlugin(XiaomingPlugin plugin);

    default Logger getLog() {
        final XiaomingPlugin plugin = getPlugin();
        if (Objects.nonNull(plugin)) {
            return plugin.getLog();
        } else {
            return LoggerFactory.getLogger(getClass());
        }
    }

    @Override
    default XiaomingBot getXiaomingBot() {
        return getPlugin().getXiaomingBot();
    }
}