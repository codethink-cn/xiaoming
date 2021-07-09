package com.chuanwise.xiaoming.core.plugin;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.language.Language;
import com.chuanwise.xiaoming.api.plugin.PluginProperty;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.language.LanguageImpl;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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