package com.chuanwise.xiaoming.core.schedule;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import com.chuanwise.xiaoming.core.schedule.task.PreservableSaveTaskImpl;
import com.chuanwise.xiaoming.core.schedule.task.ScheduableTaskImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SchedulerImpl extends ModuleObjectImpl implements Scheduler {
    @Getter
    Set<ScheduableTask<?>> plannedTasks = new CopyOnWriteArraySet<>();

    @Getter
    Set<ScheduableTask<?>> runningTasks = new CopyOnWriteArraySet<>();

    @Getter
    transient final List<Runnable> finalTasks = new LinkedList<>();

    @Getter
    transient final ExecutorService threadPool;

    @Getter
    transient final PreservableSaveTask preservableSaveTask = new PreservableSaveTaskImpl();

    public SchedulerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        this.threadPool = Executors.newFixedThreadPool(xiaomingBot.getConfiguration().getMaxMainThreadPoolSize());
    }

    protected <T> ScheduableTask<T> runImmediately(ScheduableTask<T> scheduableTask) {
        synchronized (plannedTasks) {
            plannedTasks.remove(scheduableTask);
        }
        getThreadPool().execute(() -> {
            synchronized (runningTasks) {
                runningTasks.add(scheduableTask);
            }
            scheduableTask.run();
            synchronized (runningTasks) {
                runningTasks.remove(scheduableTask);
            }
        });
        // 周期性任务派生
        if (scheduableTask.isPeriodic()) {
            try {
                final ScheduableTask<T> clonedTask = scheduableTask.clone();
                clonedTask.setTime(System.currentTimeMillis() + clonedTask.getPeriod());
                synchronized (plannedTasks) {
                    plannedTasks.add(clonedTask);
                }
            } catch (CloneNotSupportedException e) {
                getLog().error("派生周期性任务：" + scheduableTask.getDescription() + "时出现异常", e);
            }
        }
        return scheduableTask;
    }

    @Override
    public <T> ScheduableTask<T> run(ScheduableTask<T> scheduableTask) {
        scheduableTask.setXiaomingBot(getXiaomingBot());
        final Set<ScheduableTask<?>> plannedTasks = getPlannedTasks();

        // 加入就绪队列
        synchronized (plannedTasks) {
            plannedTasks.add(scheduableTask);
        }

        if (scheduableTask.isTimeout()) {
            runImmediately(scheduableTask);
        } else {
            synchronized (plannedTasks) {
                plannedTasks.notifyAll();
            }
        }
        return scheduableTask;
    }

    @Override
    public <T> ScheduableTask<T> run(Callable<T> callable) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        task.setTime(System.currentTimeMillis());
        return run(task);
    }

    @Override
    public <T> ScheduableTask<T> runLater(long delay, Callable<T> callable) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        return runLater(delay, task);
    }

    @Override
    public <T> ScheduableTask<T> periodicRun(long period, Callable<T> callable) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        task.setPeriod(period);
        task.setTime(System.currentTimeMillis());
        return run(task);
    }

    @Override
    public <T> ScheduableTask<T> periodicRunLater(long period, long delay, Callable<T> callable) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        task.setPeriod(period);
        return runLater(delay, task);
    }

    @Override
    public Logger getLog() {
        return log;
    }

    volatile boolean running = false;

    @Override
    public void stop() {
        running = false;
        synchronized (plannedTasks) {
            plannedTasks.notifyAll();
        }

        preservableSaveTask.save();
        for (Runnable finalTask : finalTasks) {
            threadPool.execute(finalTask);
        }
        threadPool.shutdown();
    }

    @Override
    public void run() {
        running = true;
        runFinally(preservableSaveTask);
        while (running && !getXiaomingBot().isStop()) {
            final boolean noAnyPlannedTasks;
            synchronized (plannedTasks) {
                noAnyPlannedTasks = plannedTasks.isEmpty();
            }

            if (!noAnyPlannedTasks) {
                // 寻找一个最近的任务
                ScheduableTask<?> nearestTask = null;
                synchronized (plannedTasks) {
                    for (ScheduableTask<?> scheduableTask : plannedTasks) {
                        if (Objects.isNull(nearestTask) || scheduableTask.getTime() < nearestTask.getTime()) {
                            nearestTask = scheduableTask;
                        }
                    }
                }
                if (Objects.isNull(nearestTask)) {
                    continue;
                }

                // 超时等待
                if (!nearestTask.isTimeout()) {
                    synchronized (plannedTasks) {
                        try {
                            plannedTasks.wait(nearestTask.getTime() - System.currentTimeMillis());
                        } catch (InterruptedException ignored) {
                        }
                    }
                }

                // 如果到时间了就说明该干活了，否则就是因为其他原因打断的，先不执行
                // 专门开一个线程去执行
                if (!nearestTask.isFinished() && nearestTask.isTimeout()) {
                    runImmediately(nearestTask);
                }
            } else {
                synchronized (plannedTasks) {
                    try {
                        plannedTasks.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }
}