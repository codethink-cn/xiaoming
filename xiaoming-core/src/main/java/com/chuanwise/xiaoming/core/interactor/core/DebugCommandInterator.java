package com.chuanwise.xiaoming.core.interactor.core;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.FilterParameter;
import com.chuanwise.xiaoming.api.annotation.Require;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;
import com.chuanwise.xiaoming.core.time.task.OptimizeTimeTask;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DebugCommandInterator extends CommandInteractorImpl {
    public DebugCommandInterator(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
    }

    @Filter("waitme {tag}")
    @Require("debug")
    public void onWaitUserMessage(XiaomingUser user, @FilterParameter("tag") String tag) {
        user.sendMessage("waiting");
        user.sendMessage("next input: {}", user.nextGroupInput(tag));
    }

    @Filter("waituser {qq} {tag}")
    @Require("debug")
    public void onWaitUserMessage(XiaomingUser user, @FilterParameter("tag") String tag, @FilterParameter("qq") long qq) {
        user.sendMessage("waiting");
        final GroupMessage message = getXiaomingBot().getReceptionistManager().getOrPutReceptionist(qq).nextGroupMessage(tag, 10000);
        user.sendMessage("next input: {}", message);
    }

    @Filter("waitgroup {tag}")
    @Require("debug")
    public void onWaitGroupMessage(XiaomingUser user, @FilterParameter("tag") String tag) {
        user.sendMessage("waiting");
        final GroupMessage message = getXiaomingBot().getContactManager().nextGroupMessage(tag, 10000);
        user.sendMessage("next input: {}", message);
    }

    @Filter("optimize")
    @Require("debug")
    public void onOptimize(XiaomingUser user) {
        getXiaomingBot().getTimeTaskManager().addTask(new OptimizeTimeTask(), TimeUnit.SECONDS.toMicros(10));
        user.sendMessage("optimized");
    }

    @Filter("repeat {message}")
    @Require("debug")
    public void onOptimize(XiaomingUser user, @FilterParameter("message") String message) {
        user.sendMessage(message);
    }
}
