package com.chuanwise.xiaoming.core.plugin;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.plugin.PluginProperty;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

import java.io.File;

@Getter
public class XiaomingPluginImpl implements XiaomingPlugin {
    @Setter
    XiaomingBot xiaomingBot;

    @Setter
    PluginProperty property;

    @Setter
    Logger logger;

    @Setter
    File dataFolder;

    @Setter
    ClassLoader classLoader;
    @Override
    public boolean onMessage(XiaomingUser user) {
        return false;
    }
}