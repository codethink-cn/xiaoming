package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

import java.util.Objects;

@Data
public class SimpleListeners<T extends Plugin> implements Listeners<T>, PluginObject<T> {
    protected transient XiaomingBot xiaomingBot;
    protected transient T plugin;

    @Override
    public XiaomingBot getXiaomingBot() {
        if (Objects.nonNull(xiaomingBot)) {
            return xiaomingBot;
        } else if (Objects.nonNull(plugin)) {
            return plugin.getXiaomingBot();
        } else {
            return null;
        }
    }
}
