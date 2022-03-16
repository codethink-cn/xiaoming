package cn.codethink.xiaoming.concurrent;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * BotFuture 适配器
 *
 * @see BotFuture
 * @author Chuanwise
 */
public class BotTaskAdapter
        implements BotTask, BotObject {
    
    protected final BotTask task;
    
    public BotTaskAdapter(BotTask task) {
        Preconditions.namedArgumentNonNull(task, "task");
        
        this.task = task;
    }
    
    @Override
    public void addListener(BotListener botListener) {
        task.addListener(botListener);
    }
    
    @Override
    public boolean isSuccess() {
        return task.isSuccess();
    }
    
    @Override
    public boolean isFailed() {
        return task.isFailed();
    }
    
    @Override
    public Throwable getCause() {
        return task.getCause();
    }
    
    @Override
    public void sync() throws InterruptedException {
        task.sync();
    }
    
    @Override
    public void syncUninterruptibly() {
        task.syncUninterruptibly();
    }
    
    @Override
    public boolean await(long timeout) throws InterruptedException {
        return task.await(timeout);
    }
    
    @Override
    public boolean awaitUninterruptibly(long timeout) {
        return task.awaitUninterruptibly(timeout);
    }
    
    @Override
    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return task.await(timeout, timeUnit);
    }
    
    @Override
    public void awaitUninterruptibly(long timeout, TimeUnit timeUnit) {
        task.awaitUninterruptibly(timeout, timeUnit);
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return task.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return task.isDone();
    }
    
    
    @Override
    public Bot getBot() {
        return task.getBot();
    }
}
