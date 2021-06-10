package com.chuanwise.xiaoming.api.time;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.time.task.TimeTask;

import java.io.File;
import java.util.List;

public interface TimeTaskManager extends Runnable, HostObject, Preservable<File> {
    default void addTask(TimeTask task) {
        final List<TimeTask> tasks = getTasks();
        task.setXiaomingBot(getXiaomingBot());
        tasks.add(task);
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    default void addTask(TimeTask task, long delay) {
        final List<TimeTask> tasks = getTasks();
        task.setXiaomingBot(getXiaomingBot());
        tasks.add(task);
        task.setTime(System.currentTimeMillis() + delay);
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    default void addPeriodicTask(TimeTask task, long period, long delay) {
        final List<TimeTask> tasks = getTasks();
        task.setXiaomingBot(getXiaomingBot());
        tasks.add(task);
        task.setPeriod(period);
        task.setTime(System.currentTimeMillis() + delay);
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    List<TimeTask> getTasks();

    void stop();
}
