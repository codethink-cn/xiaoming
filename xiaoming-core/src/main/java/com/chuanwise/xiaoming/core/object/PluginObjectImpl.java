package com.chuanwise.xiaoming.core.object;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.PluginObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import lombok.Data;

import java.util.Objects;

@Data
public class PluginObjectImpl implements PluginObject {
    XiaomingPlugin plugin;

    XiaomingBot xiaomingBot;

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
