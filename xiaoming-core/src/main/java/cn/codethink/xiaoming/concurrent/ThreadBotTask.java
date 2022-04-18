package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Objects;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;

/**
 * 线程 BotTask
 *
 * @author Chuanwise
 */
public class ThreadBotTask
        extends AbstractBotTask
        implements Runnable {
    
    /**
     * 任务状态
     */
    private enum State {
        /**
         * 最初的状态
         */
        INITIALIZED,
    
        /**
         * 正在执行状态，此时 {@link #thread} 非空
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
        CANCELLED
    }
    
    /**
     * 任务状态
     */
    private volatile State state = State.INITIALIZED;
    
    /**
     * 任务线程
     */
    private volatile Thread thread;
    
    /**
     * 任务行为
     */
    private final Runnable action;
    
    /**
     * 信号量
     */
    private final Object mutex = new Object();
    
    private volatile boolean cancellingInInterrupt;
    
    public ThreadBotTask(Bot bot, Runnable action) {
        super(bot);
    
        Preconditions.nonNull(action, "action");
        
        this.action = action;
    }
    
    @Override
    @SuppressWarnings("all")
    public void run() {
        // discuss state
        switch (state) {
            case INITIALIZED:
                break;
                
            case EXECUTING:
            case SUCCESS:
            case FAILURE:
                throw new IllegalStateException("the state of task is not INITIALIZED");
                
            case CANCELLED:
                return;
                
            default:
                throw new IllegalStateException();
        }
        
        try {
            // change the state values
            state = State.EXECUTING;
            thread = Thread.currentThread();
            
            // execute
            action.run();
    
            if (state == State.EXECUTING) {
                state = State.SUCCESS;
            }
        } catch (Throwable throwable) {
            
            if (cancellingInInterrupt
                && throwable instanceof InterruptedException) {
                return;
            }
    
            state = State.FAILURE;
            cause = throwable;
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
    
    @Override
    public boolean cancel(boolean interrupt) {
        switch (state) {
            case INITIALIZED:
                
                // if task is initialized
                // just cancel it
                state = State.CANCELLED;
                synchronized (mutex) {
                    mutex.notifyAll();
                }
                
                return true;
            case EXECUTING:
                
                // if task is executing
                // and should interrupt
                // then interrupt it
                state = State.CANCELLED;
                if (interrupt) {
                    try {
                        cancellingInInterrupt = true;
                        thread.interrupt();
                    } finally {
                        cancellingInInterrupt = false;
                    }
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
    public boolean isCancelled() {
        return state == State.CANCELLED;
    }
    
    @Override
    public boolean isDone() {
        return state == State.SUCCESS
            || state == State.FAILURE;
    }
    
    @Override
    public void sync() throws InterruptedException {
        switch (state) {
            case INITIALIZED:
            case EXECUTING:
    
                Objects.await(mutex);
                
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
    
                Objects.awaitUninterruptibly(mutex);
                
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
            case EXECUTING:
            
                return Objects.await(mutex, timeout);
        
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
            case EXECUTING:
    
                return Objects.awaitUninterruptibly(mutex, timeout);
                
            case SUCCESS:
            case FAILURE:
            case CANCELLED:
                return false;
                
            default:
                throw new IllegalStateException();
        }
    }
}
