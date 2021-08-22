package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.beans.Transient;
import java.util.Objects;

@Getter
@Setter
public class PluginObjectImpl<T extends Plugin> implements PluginObject<T> {
    protected transient T plugin;

    protected transient XiaomingBot xiaomingBot;

    @Override
    @Transient
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
