package com.chuanwise.xiaoming.core.time;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.time.TimeTaskManager;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import com.chuanwise.xiaoming.api.time.task.TimeTask;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Data
public class TimeTaskManagerImpl extends JsonFilePreservable implements TimeTaskManager {
    List<TimeTask> tasks = new CopyOnWriteArrayList<>();

    Set<TimeTask> histories = new HashSet<>();

    XiaomingBot xiaomingBot;

    @Override
    public Logger getLog() {
        return log;
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
                    histories.add(nearestTask);
                    if (nearestTask.isPeriodic()) {
                        try {
                            final TimeTask nextTimeTask = (TimeTask) nearestTask.clone();
                            nextTimeTask.setTime(System.currentTimeMillis() + nearestTask.getPeriod());
                            addTask(nextTimeTask);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
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

    public void setTasks(List<TimeTask> tasks) {
        this.tasks = tasks;
        for (TimeTask task : tasks) {
            task.setXiaomingBot(getXiaomingBot());
        }
    }
}
