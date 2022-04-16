package cn.codethink.xiaoming.concurrent;

import cn.codethink.common.util.Objects;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;

/**
 * 周期性线程 BotTask
 *
 * @author Chuanwise
 */
public class PeriodThreadBotTask
        extends AbstractBotTask
        implements Runnable, PeriodBotTask {
    
    /**
     * 任务状态
     */
    private enum State {
        /**
         * 正在执行
         */
        EXECUTING,
    
        /**
         * 正在等待被执行
         */
        WAITING,
    
        /**
         * 已被取消
         */
        CANCELLED,
    
        /**
         * 只取消下一次
         */
        SKIPPING,
    
        /**
         * 出现异常
         */
        FAILURE,
    }
    
    /**
     * 任务状态
     */
    private volatile State state = State.WAITING;
    
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
    
    public PeriodThreadBotTask(Bot bot, Runnable action) {
        super(bot);
    
        Preconditions.nonNull(action, "action");
        
        this.action = action;
    }
    
    @Override
    @SuppressWarnings("all")
    public void run() {
        // discuss state
        switch (state) {
            case WAITING:
                break;
            case SKIPPING:
                state = State.WAITING;
                return;
            case CANCELLED:
            case FAILURE:
                return;
            case EXECUTING:
                throw new IllegalStateException("can not execute a period task in parallel");
            default:
                throw new IllegalStateException();
        }
        
        try {
            // 改变状态量
            state = State.EXECUTING;
            thread = Thread.currentThread();
            
            // 执行
            action.run();
    
            // 判断是否其他地方取消了，并且没有打断
            if (state == State.EXECUTING) {
                state = State.WAITING;
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
            
            // 执行监听器
            for (BotTaskListener listener : listeners) {
                listener.listen(this);
            }
        }
    }
    
    @Override
    public boolean cancel(boolean interrupt) {
        switch (state) {
            case WAITING:
            case SKIPPING:
                state = State.CANCELLED;
                return true;
            case EXECUTING:
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
            case FAILURE:
            case CANCELLED:
                return false;
            default:
                throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean skip() {
        switch (state) {
            case WAITING:
            case SKIPPING:
            case EXECUTING:
                state = State.SKIPPING;
                return true;
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
    public boolean isSkipping() {
        return state == State.SKIPPING;
    }
    
    @Override
    public boolean isDone() {
        return false;
    }
    
    @Override
    public void sync() throws InterruptedException {
        switch (state) {
            case WAITING:
            case SKIPPING:
            case EXECUTING:
                
                Objects.await(mutex);
                
                break;
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
            case WAITING:
            case SKIPPING:
            case EXECUTING:
            
                Objects.awaitUninterruptibly(mutex);
            
                break;
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
            case WAITING:
            case SKIPPING:
            case EXECUTING:
    
                return Objects.await(mutex, timeout);
                
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
            case WAITING:
            case SKIPPING:
            case EXECUTING:
            
                return Objects.awaitUninterruptibly(mutex, timeout);
        
            case FAILURE:
            case CANCELLED:
                return false;
            default:
                throw new IllegalStateException();
        }
    }
}
