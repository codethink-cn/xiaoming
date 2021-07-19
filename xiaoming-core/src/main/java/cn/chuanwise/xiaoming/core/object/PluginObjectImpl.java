package cn.chuanwise.xiaoming.core.object;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.object.PluginObject;
import cn.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PluginObjectImpl implements PluginObject {
    protected transient XiaomingPlugin plugin;

    protected transient XiaomingBot xiaomingBot;

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
