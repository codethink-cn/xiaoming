package com.chuanwise.xiaoming.core.schedule;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.core.object.ModuleObjectImpl;
import com.chuanwise.xiaoming.core.schedule.task.PreservableSaveTaskImpl;
import com.chuanwise.xiaoming.core.schedule.task.ScheduableTaskImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SchedulerImpl extends ModuleObjectImpl implements Scheduler {
    @Getter
    Set<ScheduableTask<?>> plannedTasks = new LinkedHashSet<>();

    @Getter
    Set<ScheduableTask<?>> runningTasks = new LinkedHashSet<>();

    @Getter
    transient List<Runnable> finalTasks = new LinkedList<>();

    @Getter
    transient ExecutorService threadPool = Executors.newCachedThreadPool();

    @Getter
    transient PreservableSaveTask preservableSaveTask = new PreservableSaveTaskImpl();

    public SchedulerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
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
        return scheduableTask;
    }

    @Override
    public <T> ScheduableTask<T> run(ScheduableTask<T> scheduableTask) {
        scheduableTask.setXiaomingBot(getXiaomingBot());
        final Set<ScheduableTask<?>> tasks = this.getPlannedTasks();

        // 加入就绪队列
        synchronized (tasks) {
            tasks.add(scheduableTask);
        }

        if (scheduableTask.isTimeout()) {
            runImmediately(scheduableTask);
        } else {
            synchronized (tasks) {
                tasks.notifyAll();
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
    public <T> ScheduableTask<T> runLater(Callable<T> callable, long delay) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        return runLater(task, delay);
    }

    @Override
    public <T> ScheduableTask<T> periodicRun(Callable<T> callable, long period) {
        final ScheduableTaskImpl<T> task = new ScheduableTaskImpl<>();
        task.setCallable(callable);
        task.setPeriod(period);
        task.setTime(System.currentTimeMillis());
        return run(task);
    }

    @Override
    public <T> ScheduableTask<T> periodicRunLater(Callable<T> callable, long period, long delay) {
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
        synchronized (plannedTasks) {
            plannedTasks.notifyAll();
        }



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
                if (!nearestTask.isFinished() && plannedTasks.contains(nearestTask) && nearestTask.isTimeout()) {
                    runImmediately(nearestTask);

                    if (nearestTask.isPeriodic()) {
                        try {
                            runLater(nearestTask.clone(), nearestTask.getPeriod());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
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