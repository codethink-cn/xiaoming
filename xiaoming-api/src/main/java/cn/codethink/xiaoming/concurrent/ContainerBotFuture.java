package cn.codethink.xiaoming.concurrent;

import cn.codethink.util.Executors;
import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.Bot;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 容器 BotFuture，通过 set 来设置状态。
 *
 * @author Chuanwise
 */
public class ContainerBotFuture<T>
        extends AbstractBotFuture<T> {
    
    public ContainerBotFuture(Bot bot) {
        super(bot);
    }
    
    private enum State {
        /**
         * 初始化时的 BotFuture 状态
         */
        INITIALIZED,
    
        /**
         * 执行成功时的状态，此时 {@link #value} 才有意义
         */
        SUCCESS,
    
        /**
         * 执行失败时的状态，此时 {@link #cause} 非空
         */
        FAILURE,
    
        /**
         * 被取消的状态
         */
        CANCELLED
    }
    
    /**
     * BotFuture 的状态
     */
    private volatile State state = State.INITIALIZED;
    
    /**
     * BotFuture 的值
     */
    private volatile T value;
    
    /**
     * 状态改变信号量
     */
    private final Object mutex = new Object();
    
    @Override
    public void sync() throws InterruptedException {
        switch (state) {
            case INITIALIZED:
                
                Executors.await(mutex);
                
                break;
            case SUCCESS:
            case FAILURE:
            case CANCELLED:
                break;
            default:
                throw new IllegalStateException();
        }
    }
    
    @Override
    public void syncUninterruptibly() {
        switch (state) {
            case INITIALIZED:
            
                Executors.awaitUninterruptibly(mutex);
            
                break;
            case SUCCESS:
            case FAILURE:
            case CANCELLED:
                break;
            default:
                throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean await(long timeout) throws InterruptedException {
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
    
        switch (state) {
            case INITIALIZED:
    
                return Executors.await(mutex, timeout);
                
            case SUCCESS:
            case FAILURE:
            case CANCELLED:
                return false;
            default:
                throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean awaitUninterruptibly(long timeout) {
        Preconditions.argument(timeout >= 0, "timeout must be bigger than or equals to 0!");
    
        switch (state) {
            case INITIALIZED:
            
                return Executors.awaitUninterruptibly(mutex, timeout);
        
            case SUCCESS:
            case FAILURE:
            case CANCELLED:
                return false;
            default:
                throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        switch (state) {
            case INITIALIZED:
                
                state = State.CANCELLED;
                return true;
                
            case SUCCESS:
            case FAILURE:
            case CANCELLED:
                return false;
                
            default:
                throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean isCancelled() {
        return state == State.CANCELLED;
    }
    
    @Override
    public boolean isDone() {
        return state == State.SUCCESS
            || state == State.FAILURE;
    }
    
    public void setCause(Throwable cause) {
        Preconditions.namedArgumentNonNull(cause, "cause");
        Preconditions.state(state == State.INITIALIZED, "state of the bot future is not INITIALIZED");
        
        this.cause = cause;
        
        state = State.FAILURE;
    
        synchronized (mutex) {
            mutex.notifyAll();
        }
    
        for (BotListener listener : listeners) {
            listener.operationComplete(this);
        }
    }
    
    public void setValue(T value) {
        Preconditions.state(state == State.INITIALIZED, "state of the bot future is not INITIALIZED");
    
        this.value = value;
    
        state = State.SUCCESS;
    
        synchronized (mutex) {
            mutex.notifyAll();
        }
    
        for (BotListener listener : listeners) {
            listener.operationComplete(this);
        }
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException {
        sync();
    
        if (Objects.nonNull(cause)) {
            throw new ExecutionException(cause);
        }
    
        return value;
    }
    
    @Override
    public T get(long timeMillis) throws InterruptedException, ExecutionException, TimeoutException {
        if (state == State.INITIALIZED
            && !await(timeMillis)) {
            throw new TimeoutException();
        }
    
        if (Objects.nonNull(cause)) {
            throw new ExecutionException(cause);
        }
    
        return value;
    }
}
