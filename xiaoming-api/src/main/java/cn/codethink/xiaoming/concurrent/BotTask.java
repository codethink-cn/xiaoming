package cn.codethink.xiaoming.concurrent;

import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.TimeUnit;

/**
 * 调度器控制器
 *
 * @author Chuanwise
 */
public interface BotTask
        extends BotObject {
    
    /**
     * 取消准备调度的任务
     *
     * @param interrupt 当任务正在执行时是否打断
     * @return 如果已经执行完成或已经被取消，返回 false，否则返回 true
     */
    boolean cancel(boolean interrupt);
    
    /**
     * 获取任务是否执行结束
     *
     * @return 任务是否执行结束
     */
    boolean isDone();
    
    /**
     * 添加执行监听器。
     * 当执行完成时，该操作会立即执行。
     *
     * @param botTaskListener 执行监听器
     */
    void addListener(BotTaskListener botTaskListener);
    
    /**
     * 询问执行是否成功
     *
     * @return 执行是否成功
     */
    boolean isSucceed();
    
    /**
     * 询问执行是否失败
     *
     * @return 执行是否失败
     */
    boolean isFailed();
    
    /**
     * 询问执行是否被取消
     *
     * @return 执行是否被取消
     */
    boolean isCancelled();
    
    /**
     * 获取失败异常信息
     *
     * @return 如果执行成功，返回 null，否则是异常信息。
     */
    Throwable getCause();
    
    /**
     * 同步
     *
     * @throws InterruptedException 中断异常
     */
    void sync() throws InterruptedException;
    
    /**
     * 不可中断的同步
     */
    void syncUninterruptibly();
    
    /**
     * 不可中断地等待
     *
     * @param timeout 超时时长
     * @throws InterruptedException 中断异常
     * @return 是否执行结束
     */
    boolean await(long timeout) throws InterruptedException;
    
    /**
     * 不可中断地等待
     *
     * @param timeout 超时时长
     * @return 是否执行结束
     */
    boolean awaitUninterruptibly(long timeout);
    
    /**
     * 等待执行
     *
     * @param timeout 超时时长
     * @param timeUnit 时间单位
     * @throws InterruptedException 中断异常
     * @return 是否执行结束
     */
    boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    /**
     * 不可中断地等待
     *
     * @param timeout 超时时长
     * @param timeUnit 时间单位
     */
    void awaitUninterruptibly(long timeout, TimeUnit timeUnit);
}
