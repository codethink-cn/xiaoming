package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.utility.CheckUtility;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    default <T> ScheduledFuture<T> runLater(long delay, Runnable runnable, T returnValue) {
        return runLater(delay, () -> {
            runnable.run();
            return returnValue;
        });
    }

    default ScheduledFuture<?> runAtFixedRate(long period, Runnable runnable) {
        return runAtFixedRateLater(period, 0, runnable);
    }

    default ScheduledFuture<?> runAtFixedRateLater(long period, long delay, Runnable runnable) {
        return getThreadPool().scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    default ScheduledFuture<?> runWithFixedDelay(long period, Runnable runnable) {
        return runWithFixedDelayLater(period, 0, runnable);
    }

    default ScheduledFuture<?> runWithFixedDelayLater(long period, long delay, Runnable runnable) {
        return getThreadPool().scheduleWithFixedDelay(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    default <T> ScheduledFuture<T> runLater(long delay, Callable<T> callable) {
        return getThreadPool().schedule(callable, delay, TimeUnit.MILLISECONDS);
    }

    default Runnable getFinalTask(String name) {
        return getFinalTasks().get(name);
    }

    Map<String, Runnable> getFinalTasks();

    void runFinally(String name, Runnable runnable);

    Runnable cancelFinally(String name);

    ScheduledExecutorService getThreadPool();

    default boolean isRunning() {
        return !isStopped();
    }

    default boolean isStopped() {
        return getThreadPool().isShutdown();
    }

    default void stop() {
        CheckUtility.checkState(!isStopped(), "scheduler already stopped");
        final Map<String, Runnable> finalTasks = getFinalTasks();
        finalTasks.forEach((name, runnable) -> run(runnable));
        getThreadPool().shutdown();
    }

    default void stopNow() {
        CheckUtility.checkState(!isStopped(), "scheduler already stopped");
        final Map<String, Runnable> finalTasks = getFinalTasks();
        final List<Future<?>> futures = new ArrayList<>(finalTasks.size());

        finalTasks.forEach((name, runnable) -> {
            getLogger().info("正在执行关闭前任务：" + name);
            futures.add(run(runnable, null));
        });
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                getLogger().error("等待任务执行结束时出现异常", exception);
            } catch (ExecutionException e) {
                getLogger().error("执行关闭前任务时出现异常", e);
            }
        }
        getThreadPool().shutdownNow();
    }

    default boolean awaitStop(long timeout) throws InterruptedException {
        CheckUtility.checkState(!isStopped(), "scheduler already stopped");
        final Map<String, Runnable> finalTasks = getFinalTasks();
        final List<Future<?>> futures = new ArrayList<>(finalTasks.size());

        finalTasks.forEach((name, runnable) -> {
            getLogger().info("正在执行关闭前任务：" + name);
            futures.add(run(runnable, null));
        });
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                getLogger().error("等待任务执行结束时出现异常", exception);
            } catch (ExecutionException e) {
                getLogger().error("执行关闭前任务时出现异常", e);
            }
        }
        return getThreadPool().awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }
}
