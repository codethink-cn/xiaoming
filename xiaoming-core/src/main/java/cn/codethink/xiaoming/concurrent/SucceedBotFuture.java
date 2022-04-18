package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

import java.util.concurrent.TimeUnit;

/**
 * 已经执行成功的 BotFuture
 *
 * @author Chuanwise
 */
public class SucceedBotFuture<T>
        extends AbstractBotObject
        implements BotFuture<T> {
    
    private final T value;
    
    public SucceedBotFuture(Bot bot, T value) {
        super(bot);
        
        this.value = value;
    }
    
    @Override
    public T get(long timeMillis) {
        return value;
    }
    
    @Override
    public void addListener(BotTaskListener botTaskListener) {
        Preconditions.nonNull(botTaskListener, "bot listener");
        
        botTaskListener.listen(this);
    }
    
    @Override
    public boolean isSucceed() {
        return true;
    }
    
    @Override
    public boolean isFailed() {
        return false;
    }
    
    @Override
    public Throwable getCause() {
        return null;
    }
    
    @Override
    public void sync() {
    }
    
    @Override
    public void syncUninterruptibly() {
    }
    
    @Override
    public boolean await(long timeout) {
        return false;
    }
    
    @Override
    public boolean awaitUninterruptibly(long timeout) {
        return false;
    }
    
    @Override
    public boolean await(long timeout, TimeUnit timeUnit) {
        return false;
    }
    
    @Override
    public void awaitUninterruptibly(long timeout, TimeUnit timeUnit) {
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return true;
    }
    
    @Override
    public T get() {
        return value;
    }
    
    @Override
    public T get(long timeout, TimeUnit unit) {
        Preconditions.nonNull(unit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
        
        return value;
    }
}
