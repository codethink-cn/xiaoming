package com.chuanwise.xiaoming.api.util;

import java.io.File;

/**
 * 文件工具
 * 请不要直接使用类似 {@code PathUtil.CONFIG_DIR} 的语句直接访问本类的成员。
 * 用户可能更改配置文件夹的位置，所以请使用类似 {@code getXiaomingBot().getConfigDirectory()} 的方式访问具体的小明机器人文件夹位置。
 *
 * @author Chuanwise
 * @see com.chuanwise.xiaoming.api.bot.XiaomingBot
 */
public class PathUtil {
    public static final File CONFIG_DIR = new File("configurations");

    public static final File PLUGIN_DIR = new File("plugins");

    public static final File ACCOUNT_DIR = new File("accounts");

    public static final File LOG_DIR = new File("logs");

    public static final File LAUNCHER_DIR = new File("launcher");

    public static final File TEXT_DIR = new File("texts");
}
