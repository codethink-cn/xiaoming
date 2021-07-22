package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.xiaoming.object.ModuleObject;

import java.util.List;
import java.util.concurrent.*;

public interface Scheduler extends ModuleObject {
    default void run(Runnable runnable) {
        getThreadPool().submit(runnable);
    }

    default <T> Future<T> run(Runnable runnable, T returnValue) {
        return getThreadPool().submit(runnable, returnValue);
    }

    default <T> Future<T> run(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }

    default ScheduledFuture<?> runLater(long delay, Runnable runnable) {
        return getThreadPool().schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    default ScheduledFuture<?> periodicRunAtFixedRate(long period, Runnable runnable) {
        return periodicRunAtFixedRateLater(period, 0, runnable);
    }

    default ScheduledFuture<?> periodicRunAtFixedRateLater(long period, long delay, Runnable runnable) {
        return getThreadPool().scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    default ScheduledFuture<?> periodicRunWithFixedDelay(long period, Runnable runnable) {
        return periodicRunWithFixedDelayLater(period, 0, runnable);
    }

    default ScheduledFuture<?> periodicRunWithFixedDelayLater(long period, long delay, Runnable runnable) {
        return getThreadPool().scheduleWithFixedDelay(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    default <T> ScheduledFuture<T> runLater(long delay, Callable<T> callable) {
        return getThreadPool().schedule(callable, delay, TimeUnit.MILLISECONDS);
    }

    List<Runnable> getFinalTasks();

    default void runFinally(Runnable runnable) {
        getFinalTasks().add(runnable);
    }

    default void cancelFinally(Runnable runnable) {
        if (isStopped()) {
            throw new IllegalStateException("scheduler already stopped");
        }
        getFinalTasks().remove(runnable);
    }

    ScheduledExecutorService getThreadPool();

    default boolean isRunning() {
        return !isStopped();
    }

    default boolean isStopped() {
        return getThreadPool().isShutdown();
    }

    default void stop() {
        if (isStopped()) {
            throw new IllegalStateException("scheduler already stopped");
        }
        getThreadPool().shutdown();
        getFinalTasks().forEach(runnable -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    default void stopNow() {
        if (isStopped()) {
            throw new IllegalStateException("scheduler already stopped");
        }
        getThreadPool().shutdownNow();
        getFinalTasks().forEach(runnable -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    default void awaitStop(long timeout) throws InterruptedException {
        if (isStopped()) {
            throw new IllegalStateException("scheduler already stopped");
        }
        getThreadPool().awaitTermination(timeout, TimeUnit.MILLISECONDS);
        getFinalTasks().forEach(runnable -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
