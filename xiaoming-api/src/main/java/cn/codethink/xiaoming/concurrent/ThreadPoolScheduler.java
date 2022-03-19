package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

import java.util.concurrent.*;

/**
 * 用线程池实现的调度器
 *
 * @author Chuanwise
 */
public class ThreadPoolScheduler
        extends AbstractScheduler {
    
    private final ScheduledExecutorService threadPool;
    
    @Getter
    private final int threadCount;
    
    @SuppressWarnings("all")
    public ThreadPoolScheduler(Bot bot, int threadCount) {
        super(bot);
    
        Preconditions.argument(threadCount >= 0);
        
        this.threadCount = threadCount;
        threadPool = Executors.newScheduledThreadPool(threadCount);
    }
    
    @Override
    public BotTask submit(Runnable action) {
        Preconditions.namedArgumentNonNull(action, "action");
    
        final ThreadBotTask botTask = new ThreadBotTask(bot, action);
        threadPool.submit(botTask);
    
        return botTask;
    }
    
    @Override
    public BotTask schedule(Runnable action, long delay) {
        Preconditions.namedArgumentNonNull(action, "action");
    
        final ThreadBotTask botTask = new ThreadBotTask(bot, action);
        threadPool.schedule(botTask, delay, TimeUnit.MILLISECONDS);
        
        return botTask;
    }
    
    @Override
    public <T> BotFuture<T> submit(Callable<T> action) {
        Preconditions.namedArgumentNonNull(action, "action");
    
        final ThreadBotFuture<T> future = new ThreadBotFuture<>(bot, action);
        threadPool.submit(future);
        
        return future;
    }
    
    @Override
    public <T> BotFuture<T> schedule(Callable<T> action, long delay) {
        Preconditions.namedArgumentNonNull(action, "action");
        Preconditions.argument(delay > 0, "delay must be bigger than 0!");
    
        final ThreadBotFuture<T> task = new ThreadBotFuture<>(bot, action);
        final ScheduledFuture<T> schedule = threadPool.schedule(task, delay, TimeUnit.MILLISECONDS);
    
        return new BotFutureAdapter<T>(task) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                final boolean superCancelled = super.cancel(mayInterruptIfRunning);
                final boolean scheduleCancelled = schedule.cancel(mayInterruptIfRunning);
    
                return superCancelled && scheduleCancelled;
            }
        };
    }
    
    @Override
    public PeriodBotTask scheduleWithFixedDelay(Runnable action, long period) {
        Preconditions.namedArgumentNonNull(action, "action");
        Preconditions.argument(period > 0, "period must be bigger than 0!");
        
        final PeriodThreadBotTask task = new PeriodThreadBotTask(bot, action);
        final ScheduledFuture<?> schedule = threadPool.scheduleWithFixedDelay(task, 0, period, TimeUnit.MILLISECONDS);
        
        return new PeriodBotTaskAdapter(task) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                final boolean superCancelled = super.cancel(mayInterruptIfRunning);
                final boolean scheduleCancelled = schedule.cancel(mayInterruptIfRunning);
                
                return superCancelled && scheduleCancelled;
            }
        };
    }
    
    @Override
    public PeriodBotTask scheduleAtFixedRate(Runnable action, long period) {
        Preconditions.namedArgumentNonNull(action, "action");
        Preconditions.argument(period > 0, "period must be bigger than 0!");
    
        final PeriodThreadBotTask task = new PeriodThreadBotTask(bot, action);
        final ScheduledFuture<?> schedule = threadPool.scheduleAtFixedRate(task, 0, period, TimeUnit.MILLISECONDS);
    
        return new PeriodBotTaskAdapter(task) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                final boolean superCancelled = super.cancel(mayInterruptIfRunning);
                final boolean scheduleCancelled = schedule.cancel(mayInterruptIfRunning);
            
                return superCancelled && scheduleCancelled;
            }
        };
    }
    
    @Override
    public boolean isShutdown() {
        return threadPool.isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return threadPool.isTerminated();
    }
    
    @Override
    public void shutdownGracefully() {
        threadPool.shutdown();
    }
    
    @Override
    public void shutdownImmediately() {
        threadPool.shutdownNow();
    }
    
    @Override
    public boolean awaitTermination(long timeout) throws InterruptedException {
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");

        if (timeout == 0) {
            awaitTermination();
            return true;
        } else {
            return threadPool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    @SuppressWarnings("all")
    public void awaitTermination() throws InterruptedException {
        while (!isTerminated()) {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }
}
