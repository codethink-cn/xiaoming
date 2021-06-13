package com.chuanwise.xiaoming.api.schedule;

import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public interface Scheduler extends Runnable, ModuleObject {
    default void remove(ScheduableTask<?> task) {
        final Set<ScheduableTask<?>> tasks = getPlannedTasks();
        task.cancel();
        tasks.remove(task);
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    default void start() {
        getThreadPool().execute(this);
    }

    <T> ScheduableTask<T> run(ScheduableTask<T> scheduableTask);

    default <T> ScheduableTask<T> runLater(ScheduableTask<T> scheduableTask, long delay) {
        scheduableTask.setTime(System.currentTimeMillis() + delay);
        return run(scheduableTask);
    }

    default <T> ScheduableTask<T> periodicRunLater(ScheduableTask<T> scheduableTask, long period, long delay) {
        scheduableTask.setPeriod(period);
        scheduableTask.setTime(System.currentTimeMillis() + delay);
        return run(scheduableTask);
    }

    default ScheduableTask<Boolean> run(Runnable runnable) {
        return run(() -> {
            runnable.run();
            return true;
        });
    }

    default ScheduableTask<Boolean> runLater(Runnable runnable, long delay) {
        return runLater(() -> {
            runnable.run();
            return true;
        }, delay);
    }

    <T> ScheduableTask<T> run(Callable<T> callable);

    <T> ScheduableTask<T> runLater(Callable<T> callable, long delay);

    <T> ScheduableTask<T> periodicRunLater(Callable<T> callable, long period, long delay);

    <T> ScheduableTask<T> periodicRun(Callable<T> callable, long period);

    default ScheduableTask<Boolean> periodicRunLater(Runnable runnable, long period, long delay) {
        return periodicRunLater(() -> {
            runnable.run();
            return true;
        }, period, delay);
    }

    default ScheduableTask<Boolean> periodicRun(Runnable runnable, long period) {
        return periodicRun(() -> {
            runnable.run();
            return true;
        }, period);
    }

    Set<ScheduableTask<?>> getPlannedTasks();

    Set<ScheduableTask<?>> getRunningTasks();

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
