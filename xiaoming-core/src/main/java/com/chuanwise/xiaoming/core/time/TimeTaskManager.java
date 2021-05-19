package com.chuanwise.xiaoming.core.time;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.XiaomingThread;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import com.chuanwise.xiaoming.core.time.task.TimeTask;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class TimeTaskManager extends HostObjectImpl implements XiaomingThread {
    Set<TimeTask> tasks = new CopyOnWriteArraySet<>();

    Set<TimeTask> histories = new HashSet<>();

    public TimeTaskManager(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    volatile boolean running = false;

    @Override
    public void stop() {
        running = false;
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    @Override
    public void run() {
        running = true;
        while (!getXiaomingBot().isStop() && running) {
            if (!tasks.isEmpty()) {
                // 最近任务
                TimeTask nearestTask = null;
                for (TimeTask timeTask : tasks) {
                    if (Objects.isNull(nearestTask) || timeTask.getTime() < nearestTask.getTime()) {
                        nearestTask = timeTask;
                    }
                }

                synchronized (tasks) {
                    try {
                        tasks.wait(nearestTask.getTime() - System.currentTimeMillis());
                    } catch (InterruptedException ignored) {
                    }
                }

                // 如果到时间了就说明该干活了，否则就是因为其他原因打断的，先不执行
                // 专门开一个线程去执行
                if (System.currentTimeMillis() >= nearestTask.getPeriod()) {
                    tasks.remove(nearestTask);
                    getXiaomingBot().execute(nearestTask::run);
                }
            } else {
                synchronized (tasks) {
                    try {
                        tasks.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }
}
