package com.chuanwise.xiaoming.example;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.core.bot.XiaomingBotImpl;

/**
 * 程序中调用小明示例
 * @author Chuanwise
 */
public class EnableXiaomingExample {
    public static void main(String[] args) {
        // 第一个参数为 QQ，第二个为密码
        XiaomingBot xiaomingBot = new XiaomingBotImpl(123456789, "");
        xiaomingBot.start();
    }
}