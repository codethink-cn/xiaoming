package com.taixue.xiaoming.bot.test;

import com.taixue.xiaoming.bot.host.hook.ShutdownHook;
import sun.misc.Signal;

import java.util.Scanner;

public class ShutdownHookTest {
    /*
    public static void main(String[] args) {
        // 获取系统类型
        final String osType = System.getProperties().getProperty("os.name").toLowerCase().startsWith("win") ? "INT" : "USR2";

        // 设置小明关闭监听器
        Signal sig = new Signal(osType);
        Signal.handle(sig, signal -> {
            Thread t = new Thread(new ShutdownHook(), "ShutdownHook-Thread");
            Runtime.getRuntime().addShutdownHook(t);
        });
    }*/

    public static void main(String[] args) {
        //模拟处理时间

    }
}

