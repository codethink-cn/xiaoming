package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.utility.CheckUtility;
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
    transient final Map<String, Runnable> finalTasks = new ConcurrentHashMap<>();

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

    @Override
    public Map<String, Runnable> getFinalTasks() {
        return Collections.unmodifiableMap(finalTasks);
    }

    @Override
    public void runFinally(String name, Runnable runnable) {
        CheckUtility.checkState(!isStopped(), "scheduler already stopped");
        finalTasks.put(name, runnable);
    }

    @Override
    public Runnable cancelFinally(String name) {
        CheckUtility.checkState(!isStopped(), "scheduler already stopped");

        if (Objects.isNull(name)) {
            return null;
        }
        final Runnable runnable = finalTasks.get(name);
        if (Objects.nonNull(runnable)) {
            finalTasks.remove(name);
        }
        return runnable;
    }
}