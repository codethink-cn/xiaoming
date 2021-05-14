package com.chuanwise.xiaoming.test;

import com.chuanwise.xiaoming.api.util.MD5Utils;
import com.chuanwise.xiaoming.host.config.BotAccount;
import com.chuanwise.xiaoming.host.config.LauncherConfig;

import java.io.File;

public class PluginPropertyTest {
    public static void main(String[] args) {
        LauncherConfig config = new LauncherConfig();
        config.setMedium(new File("launcher.json"));
        config.setAccount(new BotAccount(1525916855, "TAIXUEqq2017!"));
        config.save();
    }
}
