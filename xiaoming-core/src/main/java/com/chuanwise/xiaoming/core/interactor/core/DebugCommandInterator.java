package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DebugCommandInterator extends CommandInteractorImpl {
    public DebugCommandInterator(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Filter("debug")
    @Require("debug")
    public void onDebug1(XiaomingUser user, @FilterParameter("tag") String tag) {
        List<String> strings = new LinkedList<>();
        for (int i = 0; i < 21; i++) {
            strings.add(String.valueOf(i));
        }
        final String s = InteractorUtils.indexChooser(user, strings, String::toString, "（无）", "\n", 4);
        user.sendMessage("你选择的内容是：{}", s);
    }

    @Filter("debug2")
    @Require("debug")
    public void onDebug2(XiaomingUser user) {
        final Object waiter = new Object();
        user.setProperty("waiter", waiter);
        getXiaomingBot().getScheduler().run(() -> {
            try {
                System.err.println("wait");
                synchronized (waiter) {
                    waiter.wait();
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            System.err.println("end");
        });
    }

    @Filter("debug3")
    @Require("debug")
    public void onDebug3(XiaomingUser user) {
        final Object waiter = user.getProperty("waiter");
        synchronized (waiter) {
            waiter.notifyAll();
        }
    }
}