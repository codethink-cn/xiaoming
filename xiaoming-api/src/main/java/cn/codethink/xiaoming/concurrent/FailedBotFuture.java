package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 执行失败的 BotFuture
 *
 * @author Chuanwise
 */
public class FailedBotFuture<T>
        extends AbstractBotObject
        implements BotFuture<T> {
    
    private final Throwable cause;
    
    public FailedBotFuture(Bot bot, Throwable cause) {
        super(bot);
        
        Preconditions.nonNull(cause, "cause");
        
        this.cause = cause;
    }
    
    @Override
    public T get(long timeMillis) throws ExecutionException {
        throw new ExecutionException(cause);
    }
    
    @Override
    public void addListener(BotTaskListener botTaskListener) {
        Preconditions.nonNull(botTaskListener, "bot listener");
        
        botTaskListener.listen(this);
    }
    
    @Override
    public boolean isSucceed() {
        return false;
    }
    
    @Override
    public boolean isFailed() {
        return true;
    }
    
    @Override
    public Throwable getCause() {
        return cause;
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
    public T get() throws ExecutionException {
        throw new ExecutionException(cause);
    }
    
    @Override
    public T get(long timeout, TimeUnit unit) throws ExecutionException {
        Preconditions.nonNull(unit, "time unit");
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
    
        throw new ExecutionException(cause);
    }
}
