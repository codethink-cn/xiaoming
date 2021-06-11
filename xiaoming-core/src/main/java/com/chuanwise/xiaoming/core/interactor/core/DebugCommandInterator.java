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
import com.chuanwise.xiaoming.core.interactor.command.CommandInteractorImpl;

import java.util.concurrent.TimeUnit;

public class DebugCommandInterator extends CommandInteractorImpl {
    public DebugCommandInterator(XiaomingBot xiaomingBot) {
        setXiaomingBot(xiaomingBot);
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
        getXiaomingBot().getScheduler().runLater(getXiaomingBot()::optimize, TimeUnit.SECONDS.toMicros(10));
        user.sendMessage("optimized");
    }

    @Filter("repeat {message}")
    @Require("debug")
    public void onOptimize(XiaomingUser user, @FilterParameter("message") String message) {
        user.sendMessage(message);
    }

    @Filter("replyNext")
    // @Require("debug")
    public void onReplyNext(XiaomingUser user) {
        final Message message = user.getContact().nextMessage(100000);
        user.reply(message, "这是你的下一条消息");
    }
}