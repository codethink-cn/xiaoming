package cn.codethink.xiaoming.concurrent;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * BotFuture 适配器
 *
 * @see cn.codethink.xiaoming.concurrent.BotFuture
 * @author Chuanwise
 */
public class BotFutureAdapter<T>
        implements BotFuture<T>, BotObject {
    
    private final BotFuture<T> future;
    
    public BotFutureAdapter(BotFuture<T> future) {
        Preconditions.namedArgumentNonNull(future, "future");
        
        this.future = future;
    }
    
    @Override
    public T get(long timeMillis) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeMillis);
    }
    
    @Override
    public void addListener(BotListener botListener) {
        future.addListener(botListener);
    }
    
    @Override
    public boolean isSuccess() {
        return future.isSuccess();
    }
    
    @Override
    public boolean isFailed() {
        return future.isFailed();
    }
    
    @Override
    public Throwable getCause() {
        return future.getCause();
    }
    
    @Override
    public void sync() throws InterruptedException {
        future.sync();
    }
    
    @Override
    public void syncUninterruptibly() {
        future.syncUninterruptibly();
    }
    
    @Override
    public boolean await(long timeout) throws InterruptedException {
        return future.await(timeout);
    }
    
    @Override
    public boolean awaitUninterruptibly(long timeout) {
        return future.awaitUninterruptibly(timeout);
    }
    
    @Override
    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return future.await(timeout, timeUnit);
    }
    
    @Override
    public void awaitUninterruptibly(long timeout, TimeUnit timeUnit) {
        future.awaitUninterruptibly(timeout, timeUnit);
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return future.isDone();
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }
    
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }
    
    @Override
    public Bot getBot() {
        return future.getBot();
    }
}
