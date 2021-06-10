package com.chuanwise.xiaoming.core.time;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.time.TimeTaskManager;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import com.chuanwise.xiaoming.api.time.task.TimeTask;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTaskManagerImpl extends JsonFilePreservable implements TimeTaskManager {
    List<TimeTask> tasks = new CopyOnWriteArrayList<>();

    Set<TimeTask> histories = new HashSet<>();

    XiaomingBot xiaomingBot;

    @Override
    public Logger getLog() {
        return log;
    }

    volatile boolean running = false;

    public void stop() {
        running = false;
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    @Override
    public void run() {
        running = true;
        while (running) {
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
                    TimeTask finalNearestTask = nearestTask;
                    getXiaomingBot().execute(() -> {
                        try {
                            finalNearestTask.run();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            finalNearestTask.setSuccess(false);
                        }
                    });
                    histories.add(nearestTask);
                    if (nearestTask.isPeriodic()) {
                        try {
                            addTask(nearestTask.clone(), nearestTask.getPeriod());
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
}
