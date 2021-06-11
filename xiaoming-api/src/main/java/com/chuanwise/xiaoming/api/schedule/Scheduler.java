package com.chuanwise.xiaoming.api.schedule;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public interface Scheduler extends Runnable, ModuleObject, Preservable<File> {
    default void start() {
        getThreadPool().execute(this);
    }

    default <T> AsyncResult<T> run(ScheduableTask<T> scheduableTask) {
        scheduableTask.setXiaomingBot(getXiaomingBot());
        if (scheduableTask.getTime() < System.currentTimeMillis()) {
            getThreadPool().execute(scheduableTask);
        } else {
            final Set<ScheduableTask<?>> set = getTasks();
            set.add(scheduableTask);
            synchronized (set) {
                set.notifyAll();
            }
        }
        return scheduableTask;
    }

    default <T> AsyncResult<T> runLater(ScheduableTask<T> scheduableTask, long delay) {
        scheduableTask.setTime(System.currentTimeMillis() + delay);
        return run(scheduableTask);
    }

    default <T> AsyncResult<T> periodicRunLater(ScheduableTask<T> scheduableTask, long period, long delay) {
        scheduableTask.setPeriod(period);
        scheduableTask.setTime(System.currentTimeMillis() + delay);
        return run(scheduableTask);
    }

    default AsyncResult<Boolean> run(Runnable runnable) {
        return run(() -> {
            runnable.run();
            return true;
        });
    }

    default AsyncResult<Boolean> runLater(Runnable runnable, long delay) {
        return runLater(() -> {
            runnable.run();
            return true;
        }, delay);
    }

    <T> AsyncResult<T> run(Callable<T> callable);

    <T> AsyncResult<T> runLater(Callable<T> callable, long delay);

    <T> AsyncResult<T> periodicRunLater(Callable<T> callable, long period, long delay);

    <T> AsyncResult<T> periodicRun(Callable<T> callable, long period);

    default AsyncResult<Boolean> periodicRunLater(Runnable runnable, long period, long delay) {
        return periodicRunLater(() -> {
            runnable.run();
            return true;
        }, period, delay);
    }

    default AsyncResult<Boolean> periodicRun(Runnable runnable, long period) {
        return periodicRun(() -> {
            runnable.run();
            return true;
        }, period);
    }

    Set<ScheduableTask<?>> getTasks();

    List<Runnable> getFinalTasks();

    PreservableSaveTask getPreservableSaveTask();

    default void runFinally(Runnable runnable) {
        getFinalTasks().add(runnable);
    }

    default void runFinally(Callable<?> callable) {
        getFinalTasks().add(() -> {
            try {
                callable.call();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    default void readySave(Preservable<?> preservable) {
        getPreservableSaveTask().readySave(preservable);
    }

    ExecutorService getThreadPool();

    void stop();
}
