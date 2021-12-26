package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.xiaoming.object.ModuleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public interface Scheduler extends ModuleObject {
    default void run(Runnable runnable) {
        runLater(0, runnable);
    }

    default <T> Future<T> run(Runnable runnable, T returnValue) {
        return getThreadPool().submit(() -> {
            runnable.run();
            return returnValue;
        });
    }

    default <T> Future<T> run(Callable<T> callable) {
        return getThreadPool().submit(() -> {
            try {
                return callable.call();
            } catch (Throwable exception) {
                getLogger().error("执行任务 " + callable + " 时出现异常", exception);
                return null;
            }
        });
    }

    default ScheduledFuture<?> runLater(long delay, Runnable runnable) {
        return getThreadPool().schedule(() -> {
            try {
                runnable.run();
            } catch (Throwable exception) {
                getLogger().error("执行任务 " + runnable + " 时出现异常", exception);
            }
        }, delay, TimeUnit.MILLISECONDS);
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
        return getThreadPool().scheduleAtFixedRate(() -> {
            try {
                runnable.run();
            } catch (Throwable exception) {
                getLogger().error("执行周期性任务 " + runnable + " 时出现异常", exception);
            }
        }, delay, period, TimeUnit.MILLISECONDS);
    }

    default ScheduledFuture<?> runWithFixedDelay(long period, Runnable runnable) {
        return runWithFixedDelayLater(period, 0, runnable);
    }

    default ScheduledFuture<?> runWithFixedDelayLater(long period, long delay, Runnable runnable) {
        return getThreadPool().scheduleWithFixedDelay(() -> {
            try {
                runnable.run();
            } catch (Throwable exception) {
                getLogger().error("执行周期性任务 " + runnable + " 时出现异常", exception);
            }
        }, delay, period, TimeUnit.MILLISECONDS);
    }

    default <T> ScheduledFuture<T> runLater(long delay, Callable<T> callable) {
        return getThreadPool().schedule(() -> {
            try {
                return callable.call();
            } catch (Throwable exception) {
                getLogger().error("执行任务 " + callable + " 时出现异常", exception);
                return null;
            }
        }, delay, TimeUnit.MILLISECONDS);
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
        ConditionUtil.checkState(!isStopped(), "scheduler already stopped");
        final Map<String, Runnable> finalTasks = getFinalTasks();
        finalTasks.forEach((name, runnable) -> run(runnable));
        getThreadPool().shutdown();
    }

    default void stopNow() {
        ConditionUtil.checkState(!isStopped(), "scheduler already stopped");
        final Map<String, Runnable> finalTasks = getFinalTasks();
        final List<Future<?>> futures = new ArrayList<>(finalTasks.size());

        finalTasks.forEach((name, runnable) -> {
            getLogger().info("正在执行关闭前任务：" + name);
            try {
                runnable.run();
            } catch (Exception exception) {
                getLogger().error("执行关闭前任务 " + name + " 时出现异常", exception);
            }
        });
        getThreadPool().shutdownNow();
    }

    default boolean awaitStop(long timeout) throws InterruptedException {
        ConditionUtil.checkState(!isStopped(), "scheduler already stopped");
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
