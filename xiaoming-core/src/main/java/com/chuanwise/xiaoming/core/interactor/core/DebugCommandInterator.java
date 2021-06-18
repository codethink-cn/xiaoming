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

    @Filter("debug1")
    @Require("debug")
    public void onDebug1(XiaomingUser user) {
        getXiaomingBot().getScheduler().run(() -> getLog().info("normal run")).setDescription("run");
        getXiaomingBot().getScheduler().runLater(5000, () -> getLog().info("runLater: 5000")).setDescription("runLater");
        getXiaomingBot().getScheduler().periodicRun(3000, () -> getLog().info("periodicRun: 5000")).setDescription("periodicRun");
        getXiaomingBot().getScheduler().periodicRunLater(4000, 5000, () -> getLog().info("periodicRunLater: 4000, 5000")).setDescription("periodicRunLater");
    }
}