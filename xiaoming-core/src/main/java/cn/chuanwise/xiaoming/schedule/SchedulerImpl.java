package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class SchedulerImpl extends ModuleObjectImpl implements Scheduler {
    @Getter
    transient final List<Runnable> finalTasks = new LinkedList<>();

    @Getter
    transient final ScheduledExecutorService threadPool;

    public SchedulerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        this.threadPool = Executors.newScheduledThreadPool(xiaomingBot.getConfiguration().getMaxMainThreadPoolSize());
    }

    @Transient
    @Override
    public Logger getLogger() {
        return log;
    }


}