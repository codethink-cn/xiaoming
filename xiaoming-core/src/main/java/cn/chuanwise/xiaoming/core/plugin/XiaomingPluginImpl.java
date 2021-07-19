package cn.chuanwise.xiaoming.core.plugin;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.language.Language;
import cn.chuanwise.xiaoming.api.plugin.PluginProperty;
import cn.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
    Logger log = LoggerFactory.getLogger(getClass());

    @Setter
    File dataFolder;
}