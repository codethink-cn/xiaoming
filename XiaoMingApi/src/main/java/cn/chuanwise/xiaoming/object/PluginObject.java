package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 插件主体对象
 * @author Chuanwise
 */
public interface PluginObject<T extends Plugin> extends XiaomingObject {
    T getPlugin();

    void setPlugin(T plugin);

    default Logger getLogger() {
        final Plugin plugin = getPlugin();
        if (Objects.nonNull(plugin)) {
            return plugin.getLogger();
        } else {
            return LoggerFactory.getLogger(getClass().getSimpleName());
        }
    }
}