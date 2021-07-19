package cn.chuanwise.xiaoming.api.schedule;

import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import cn.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import cn.chuanwise.toolkit.preservable.Preservable;

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

    default <T> ScheduableTask<T> runLater(long delay, ScheduableTask<T> scheduableTask) {
        scheduableTask.setTime(System.currentTimeMillis() + delay);
        return run(scheduableTask);
    }

    default <T> ScheduableTask<T> periodicRunLater(long period, long delay, ScheduableTask<T> scheduableTask) {
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

    default ScheduableTask<Boolean> runLater(long delay, Runnable runnable) {
        return runLater(delay, () -> {
            runnable.run();
            return true;
        });
    }

    <T> ScheduableTask<T> run(Callable<T> callable);

    <T> ScheduableTask<T> runLater(long delay, Callable<T> callable);

    <T> ScheduableTask<T> periodicRunLater(long period, long delay, Callable<T> callable);

    <T> ScheduableTask<T> periodicRun(long period, Callable<T> callable);

    default ScheduableTask<Boolean> periodicRunLater(long period, long delay, Runnable runnable) {
        return periodicRunLater(period, delay, () -> {
            runnable.run();
            return true;
        });
    }

    default ScheduableTask<Boolean> periodicRun(long period, Runnable runnable) {
        return periodicRun(period, () -> {
            runnable.run();
            return true;
        });
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
