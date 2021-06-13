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

    @Setter
    ClassLoader classLoader;

    @Override
    public Language loadLanguage(File file) {
        language = loadFileAs(LanguageImpl.class, file);
        return language;
    }

    @Override
    public Language loadLanguageOrProduce(File file) {
        language = loadFileOrProduce(LanguageImpl.class, file, LanguageImpl::new);
        return language;
    }
}