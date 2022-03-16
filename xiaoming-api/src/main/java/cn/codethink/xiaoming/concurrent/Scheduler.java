package cn.codethink.xiaoming.concurrent;

import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 核心调度器
 *
 * @author Chuanwise
 */
public interface Scheduler
        extends BotObject {
    
    /**
     * 异步执行一个任务
     *
     * @param action 动作
     * @return 任务
     */
    BotTask submit(Runnable action);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action 动作
     * @param delay  延迟
     * @return 任务
     */
    BotTask schedule(Runnable action, long delay);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action   动作
     * @param delay    延迟
     * @param timeUnit 时间单位
     * @return 任务
     */
    BotTask schedule(Runnable action, long delay, TimeUnit timeUnit);
    
    /**
     * 异步执行一个任务
     *
     * @param action 动作
     * @return 任务
     */
    <T> BotFuture<T> submit(Callable<T> action);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action 动作
     * @param delay  延迟
     * @param <T>    动作返回类型
     * @return 任务
     */
    <T> BotFuture<T> schedule(Callable<T> action, long delay);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action   动作
     * @param delay    延迟
     * @param timeUnit 时间单位
     * @param <T>      动作返回类型
     * @return 任务
     */
    <T> BotFuture<T> schedule(Callable<T> action, long delay, TimeUnit timeUnit);
    
    /**
     * 执行一个定时任务，任务之间有相同的间隔
     *
     * @param action 动作
     * @param period 周期
     * @return 定时任务
     */
    PeriodBotTask scheduleWithFixedDelay(Runnable action, long period);
    
    /**
     * 执行一个定时任务，任务之间有相同的间隔
     *
     * @param action   动作
     * @param period   周期
     * @param timeUnit 时间单位
     * @return 定时任务
     */
    PeriodBotTask scheduleWithFixedDelay(Runnable action, long period, TimeUnit timeUnit);
    
    /**
     * 执行一个定时任务，任务之间严格按照周期调度
     *
     * @param action 动作
     * @param period 周期
     * @return 定时任务
     */
    PeriodBotTask scheduleAtFixedRate(Runnable action, long period);
    
    /**
     * 执行一个定时任务，任务之间严格按照周期调度
     *
     * @param action   动作
     * @param period   周期
     * @param timeUnit 时间单位
     * @return 定时任务
     */
    PeriodBotTask scheduleAtFixedRate(Runnable action, long period, TimeUnit timeUnit);
    
    /**
     * 询问调度器是否关闭
     *
     * @return 调度器是否关闭
     */
    boolean isShutdown();
    
    /**
     * 询问所有任务是否已执行完
     *
     * @return 所有任务是否已执行完
     */
    boolean isTerminated();
    
    /**
     * 优雅地关闭调度器
     */
    void shutdownGracefully();
    
    /**
     * 立刻关闭调度器
     */
    void shutdownImmediately();
    
    /**
     * 等待所有任务执行结束
     *
     * @param timeout 超时时长
     * @return 是否在时间范围内所有任务结束了
     * @throws InterruptedException 中断异常
     */
    boolean awaitTermination(long timeout) throws InterruptedException;
    
    /**
     * 不可打断地等待所有任务执行结束
     *
     * @param timeout 超时时长
     * @return 是否在时间范围内所有任务结束了
     */
    boolean awaitTerminationUninterruptibly(long timeout);
    
    /**
     * 等待所有任务执行结束
     *
     * @param timeout  超时时长
     * @param timeUnit 事件单位
     * @return 是否在时间范围内所有任务结束了
     * @throws InterruptedException 中断异常
     */
    boolean awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    /**
     * 不可打断地等待所有任务执行结束
     *
     * @param timeout  超时时长
     * @param timeUnit 事件单位
     * @return 是否在时间范围内所有任务结束了
     */
    boolean awaitTerminationUninterruptibly(long timeout, TimeUnit timeUnit);
    
    /**
     * 等待所有任务执行结束
     */
    void awaitTermination() throws InterruptedException;
    
    /**
     * 不可中断地等待所有任务执行结束
     */
    void awaitTerminationUninterruptibly();
    
    /**
     * 同步关闭调度器
     *
     * @throws InterruptedException 中断异常
     */
    void shutdownSync() throws InterruptedException;
    
    /**
     * 同步关闭调度器
     *
     * @param timeout 超时时长
     * @return 是否在时间范围内所有任务结束了
     * @throws InterruptedException 中断异常
     */
    boolean shutdownSync(long timeout) throws InterruptedException;
    
    /**
     * 同步关闭调度器
     *
     * @param timeout  超时时长
     * @param timeUnit 时间单位
     * @return 是否在时间范围内所有任务结束了
     * @throws InterruptedException 中断异常
     */
    boolean shutdownSync(long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    /**
     * 不可打断地同步关闭调度器
     */
    void shutdownUninterruptibly();
    
    /**
     * 不可打断地同步关闭调度器
     *
     * @param timeout 超时时长
     * @return 是否在时间范围内所有任务结束了
     */
    boolean shutdownUninterruptibly(long timeout);
    
    /**
     * 不可打断地同步关闭调度器
     *
     * @param timeout  超时时长
     * @param timeUnit 时间单位
     * @return 是否在时间范围内所有任务结束了
     */
    boolean shutdownUninterruptibly(long timeout, TimeUnit timeUnit);
}