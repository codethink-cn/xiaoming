package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Executors;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 线程 BotFuture
 *
 * @author Chuanwise
 */
public class ThreadBotFuture<T>
        extends AbstractBotFuture<T>
        implements Callable<T> {
    
    /**
     * BotFuture 状态
     */
    private enum State {
        /**
         * 初始化状态
         */
        INITIALIZED,
    
        /**
         * 正在执行状态
         */
        EXECUTING,
    
        /**
         * 执行成功状态
         */
        SUCCESS,
    
        /**
         * 执行失败状态
         */
        FAILURE,
    
        /**
         * 执行取消状态
         */
        CANCELLED,
    }
    
    /**
     * BotFuture 状态
     */
    private volatile State state = State.INITIALIZED;
    
    /**
     * 执行操作
     */
    @Getter
    private final Callable<T> action;
    
    /**
     * 结果值，只有在 {@link #state} 为 {@link State#SUCCESS} 时有意义
     */
    private volatile T value;
    
    /**
     * 执行结束信号量
     */
    private final Object mutex = new Object();
    
    /**
     * 正在执行的线程，只有在 {@link #state} 为 {@link State#EXECUTING} 时非空
     */
    private volatile Thread thread;
    
    public ThreadBotFuture(Bot bot, Callable<T> action) {
        super(bot);
    
        Preconditions.namedArgumentNonNull(action, "action");
        
        this.action = action;
    }
    
    @Override
    public T get(long timeMillis) throws InterruptedException, ExecutionException, TimeoutException {
        // 如果还没有开始执行，则等待执行结束
        // 如果超时，抛出超时异常
        if (state == State.INITIALIZED
            && !await(timeMillis)) {
            throw new TimeoutException();
        }
    
        // 如果执行失败，抛出运行时错误异常
        if (Objects.nonNull(cause)) {
            throw new ExecutionException(cause);
        }
        
        // 如果被取消或执行成功，返回值
        return value;
    }
    
    @Override
    public boolean cancel(boolean interrupt) {
        switch (state) {
            case INITIALIZED:
                state = State.CANCELLED;
                synchronized (mutex) {
                    mutex.notifyAll();
                }
                return false;
            case EXECUTING:
                state = State.CANCELLED;
                if (interrupt) {
                    thread.interrupt();
                }
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
    public boolean isDone() {
        return state == State.SUCCESS
            || state == State.FAILURE;
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException {
        // 如果还没有开始执行，则等待执行结束
        // 如果超时，抛出超时异常
        if (state == State.INITIALIZED) {
            sync();
        }
    
        // 如果执行失败，抛出运行时错误异常
        if (Objects.nonNull(cause)) {
            throw new ExecutionException(cause);
        }
    
        // 如果被取消或执行成功，返回值
        return value;
    }
    
    @Override
    public boolean isCancelled() {
        return state == State.CANCELLED;
    }
    
    @Override
    public void sync() throws InterruptedException {
        switch (state) {
            case INITIALIZED:
            case EXECUTING:
    
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
            case EXECUTING:
    
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
        switch (state) {
            case INITIALIZED:
            case EXECUTING:
    
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
        switch (state) {
            case INITIALIZED:
            case EXECUTING:
    
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
    public T call() throws Exception {
        switch (state) {
            case INITIALIZED:
                break;
                
            case EXECUTING:
            case SUCCESS:
            case FAILURE:
                
                throw new IllegalStateException("state of the bot future is not INITIALIZED");
                
            case CANCELLED:
                return null;
            default:
                throw new IllegalStateException();
        }
        
        try {
            // 修改状态量
            state = State.EXECUTING;
            thread = Thread.currentThread();
            
            // 设置返回值
            value = action.call();
            
            // 设置成功状态
            if (state == State.EXECUTING) {
                state = State.SUCCESS;
            }
            
            return value;
        } catch (Throwable throwable) {
            
            // 检查是否是被取消时出现的中断异常
            if (throwable instanceof InterruptedException
                && state == State.CANCELLED) {
                return null;
            }
            
            state = State.FAILURE;
            cause = throwable;
            
            return null;
        } finally {
            thread = null;
    
            synchronized (mutex) {
                mutex.notifyAll();
            }
    
            // 执行监听器
            for (BotTaskListener listener : listeners) {
                listener.listen(this);
            }
        }
    }
}
