package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.plugin.PluginProperty;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

@Getter
public class XiaomingPluginImpl implements XiaomingPlugin {
    @Setter
    XiaomingBot xiaomingBot;

    PluginProperty property;

    @Setter
    Language language;

    @Override
    public void setProperty(PluginProperty property) {
        this.property = property;
        property.setPlugin(this);
    }

    @Setter
    Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    File dataFolder;

    @Override
    public boolean equals(Object o) {
        return Objects.equals(o.getClass(), getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}