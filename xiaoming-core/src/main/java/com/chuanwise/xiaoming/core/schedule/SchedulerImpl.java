package com.chuanwise.xiaoming.core.schedule;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import com.chuanwise.xiaoming.core.schedule.task.PreservableSaveTaskImpl;
import com.chuanwise.xiaoming.core.schedule.task.ScheduableTaskImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerImpl extends JsonFilePreservable implements Scheduler {
    @Getter
    Set<ScheduableTask<?>> tasks = new LinkedHashSet<>();

    @Getter
    transient List<Runnable> finalTasks = new LinkedList<>();

    @Getter
    @Setter
    transient XiaomingBot xiaomingBot;

    @Getter
    transient ExecutorService threadPool = Executors.newCachedThreadPool();
    transient ScheduableTask<?> recentTask;

    @Getter
    transient PreservableSaveTask preservableSaveTask = new PreservableSaveTaskImpl();

    @Override
    public <T> AsyncResult<T> run(Callable<T> callable) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        getThreadPool().execute(task);
        return task;
    }

    @Override
    public <T> AsyncResult<T> runLater(Callable<T> callable, long delay) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        return runLater(task, delay);
    }

    @Override
    public <T> AsyncResult<T> periodicRun(Callable<T> callable, long period) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        task.setPeriod(period);
        task.setTime(System.currentTimeMillis());
        return run(task);
    }

    @Override
    public <T> AsyncResult<T> periodicRunLater(Callable<T> callable, long period, long delay) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        task.setPeriod(period);
        return runLater(task, delay);
    }

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

        for (Runnable finalTask : finalTasks) {
            threadPool.execute(finalTask::run);
        }

        threadPool.shutdown();
    }

    @Override
    public void run() {
        running = true;
        runFinally(preservableSaveTask);
        while (running) {
            if (!tasks.isEmpty()) {
                // 最近任务
                ScheduableTask<?> nearestTask = null;
                for (ScheduableTask<?> scheduableTask : tasks) {
                    if (Objects.isNull(nearestTask) || scheduableTask.getTime() < nearestTask.getTime()) {
                        nearestTask = scheduableTask;
                    }
                }

                if (nearestTask.getTime() > System.currentTimeMillis()) {
                    synchronized (tasks) {
                        try {
                            tasks.wait(nearestTask.getTime() - System.currentTimeMillis());
                        } catch (InterruptedException ignored) {
                        }
                    }
                }

                // 如果到时间了就说明该干活了，否则就是因为其他原因打断的，先不执行
                // 专门开一个线程去执行
                if (System.currentTimeMillis() >= nearestTask.getTime()) {
                    tasks.remove(nearestTask);
                    getThreadPool().execute(nearestTask);
                    if (nearestTask.isPeriodic()) {
                        try {
                            runLater(nearestTask.clone(), nearestTask.getPeriod());
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
