package cn.codethink.xiaoming.concurrent;

import cn.chuanwise.common.concurrent.ThreadPoolScheduler;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SchedulerImpl
    extends ThreadPoolScheduler
    implements BotObject, Scheduler {
    
    private final Bot bot;
    
    public SchedulerImpl(Bot bot, int threadCount) {
        super(threadCount);
    
        Preconditions.objectNonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    @Override
    public Bot getBot() {
        return bot;
    }
    
    @Override
    public <T> BotPromise<T> submit(Callable<T> action) {
        return new BotPromiseAdapter<>(bot, super.submit(action));
    }
    
    @Override
    public BotTask submit(Runnable action) {
        return new BotTaskAdapter(bot, super.submit(action));
    }
    
    @Override
    public PeriodicBotTask scheduleAtFixedRate(Runnable action, long period) {
        return new PeriodicBotTaskAdapter(bot, super.scheduleAtFixedRate(action, period));
    }
    
    @Override
    public PeriodicBotTask scheduleAtFixedRate(Runnable action, long period, TimeUnit timeUnit) {
        return new PeriodicBotTaskAdapter(bot, super.scheduleAtFixedRate(action, period, timeUnit));
    }
    
    @Override
    public PeriodicBotTask scheduleWithFixedDelay(Runnable action, long period) {
        return new PeriodicBotTaskAdapter(bot, super.scheduleWithFixedDelay(action, period));
    }
    
    @Override
    public PeriodicBotTask scheduleWithFixedDelay(Runnable action, long period, TimeUnit timeUnit) {
        return new PeriodicBotTaskAdapter(bot, super.scheduleWithFixedDelay(action, period, timeUnit));
    }
    
    @Override
    public <T> BotPromise<T> schedule(Callable<T> action, long delay) {
        return new BotPromiseAdapter<>(bot, super.schedule(action, delay));
    }
    
    @Override
    public <T> BotPromise<T> schedule(Callable<T> action, long delay, TimeUnit timeUnit) {
        return new BotPromiseAdapter<>(bot, super.schedule(action, delay, timeUnit));
    }
    
    @Override
    public BotTask schedule(Runnable action, long delay) {
        return new BotTaskAdapter(bot, super.schedule(action, delay));
    }
    
    @Override
    public BotTask schedule(Runnable action, long delay, TimeUnit timeUnit) {
        return new BotTaskAdapter(bot, super.schedule(action, delay, timeUnit));
    }
}
