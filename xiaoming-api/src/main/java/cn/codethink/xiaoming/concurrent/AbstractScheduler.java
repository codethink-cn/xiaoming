package cn.codethink.xiaoming.concurrent;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @see cn.codethink.xiaoming.concurrent.Scheduler
 * @author Chuanwise
 */
public abstract class AbstractScheduler
        extends AbstractBotObject
        implements Scheduler {
    
    public AbstractScheduler(Bot bot) {
        super(bot);
    }
    
    @Override
    public BotTask schedule(Runnable action, long delay, TimeUnit timeUnit) {
        return schedule(action, timeUnit.toMillis(delay));
    }
    
    @Override
    public <T> BotFuture<T> schedule(Callable<T> action, long delay, TimeUnit timeUnit) {
        return schedule(action, timeUnit.toMillis(delay));
    }
    
    
    @Override
    public PeriodBotTask scheduleWithFixedDelay(Runnable action, long period, TimeUnit timeUnit) {
        Preconditions.namedArgumentNonNull(action, "action");
        Preconditions.namedArgumentNonNull(timeUnit, "time unit");
        Preconditions.argument(period > 0, "period must be bigger than 0!");

        return scheduleWithFixedDelay(action, timeUnit.toMillis(period));
    }
    
    @Override
    public PeriodBotTask scheduleAtFixedRate(Runnable action, long period, TimeUnit timeUnit) {
        Preconditions.namedArgumentNonNull(action, "action");
        Preconditions.namedArgumentNonNull(timeUnit, "time unit");
        Preconditions.argument(period > 0, "period must be bigger than 0!");
        
        return scheduleAtFixedRate(action, timeUnit.toMillis(period));
    }
    
    @Override
    public boolean awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Preconditions.namedArgumentNonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
    
        return awaitTermination(timeUnit.toMillis(timeout));
    }
    
    @Override
    public void awaitTerminationUninterruptibly() {
        while (!isTerminated()) {
            try {
                awaitTermination();
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    @Override
    public boolean awaitTerminationUninterruptibly(long timeout) {
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
        
        if (timeout == 0) {
            awaitTerminationUninterruptibly();
            return true;
        }
    
        final long deadline = System.currentTimeMillis() + timeout;
        while (!isTerminated()) {
            final long remain = deadline - System.currentTimeMillis();
            if (remain > 0) {
                try {
                    awaitTermination(timeout);
                } catch (InterruptedException ignored) {
                }
            } else {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean awaitTerminationUninterruptibly(long timeout, TimeUnit timeUnit) {
        Preconditions.namedArgumentNonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
    
        return awaitTerminationUninterruptibly(timeUnit.toMillis(timeout));
    }
    
    @Override
    public void shutdownSync() throws InterruptedException {
        shutdownGracefully();
        awaitTermination();
    }
    
    @Override
    public boolean shutdownSync(long timeout) throws InterruptedException {
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
        
        shutdownGracefully();
        return awaitTermination(timeout);
    }
    
    @Override
    public boolean shutdownSync(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Preconditions.namedArgumentNonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
        
        shutdownGracefully();
        return awaitTermination(timeout, timeUnit);
    }
    
    @Override
    public boolean shutdownUninterruptibly(long timeout) {
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
        
        shutdownGracefully();
        return awaitTerminationUninterruptibly(timeout);
    }
    
    @Override
    public boolean shutdownUninterruptibly(long timeout, TimeUnit timeUnit) {
        Preconditions.namedArgumentNonNull(timeUnit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
    
        shutdownGracefully();
        return awaitTerminationUninterruptibly(timeout, timeUnit);
    }
    
    @Override
    public void shutdownUninterruptibly() {
        shutdownGracefully();
        awaitTerminationUninterruptibly();
    }
}
