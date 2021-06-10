package com.chuanwise.xiaoming.api.util;

import java.io.File;

/**
 * 文件工具
 * 请不要直接使用类似 {@code PathUtils.CONFIG_DIR} 的语句直接访问本类的成员。
 * 用户可能更改配置文件夹的位置，所以请使用类似 {@code getXiaomingBot().getConfigDirectory()} 的方式访问具体的小明机器人文件夹位置。
 *
 * @author Chuanwise
 * @see com.chuanwise.xiaoming.api.bot.XiaomingBot
 */
public class PathUtils extends Utils {
    public static final File CONFIG = new File("configurations");

    public static final File PLUGIN = new File("plugins");

    public static final File ACCOUNT = new File("accounts");

    public static final File LOG = new File("logs");

    public static final File LAUNCHER = new File("launcher");

    public static final File TEXT = new File("texts");

    public static final File RESOURCES = new File("resources");
}
