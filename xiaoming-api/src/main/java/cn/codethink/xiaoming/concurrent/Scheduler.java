package cn.codethink.xiaoming.concurrent;

import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface Scheduler
    extends cn.chuanwise.common.concurrent.Scheduler, BotObject {
    
    /**
     * 异步执行一个任务
     *
     * @param action 动作
     * @return 任务
     */
    @Override
    BotTask submit(Runnable action);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action 动作
     * @param delay  延迟
     * @return 任务
     */
    @Override
    BotTask schedule(Runnable action, long delay);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action   动作
     * @param delay    延迟
     * @param timeUnit 时间单位
     * @return 任务
     */
    @Override
    BotTask schedule(Runnable action, long delay, TimeUnit timeUnit);
    
    /**
     * 异步执行一个任务
     *
     * @param action 动作
     * @return 任务
     */
    @Override
    <T> BotPromise<T> submit(Callable<T> action);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action 动作
     * @param delay  延迟
     * @param <T>    动作返回类型
     * @return 任务
     */
    @Override
    <T> BotPromise<T> schedule(Callable<T> action, long delay);
    
    /**
     * 稍后异步执行一个任务
     *
     * @param action   动作
     * @param delay    延迟
     * @param timeUnit 时间单位
     * @param <T>      动作返回类型
     * @return 任务
     */
    @Override
    <T> BotPromise<T> schedule(Callable<T> action, long delay, TimeUnit timeUnit);
    
    /**
     * 执行一个定时任务，任务之间有相同的间隔
     *
     * @param action 动作
     * @param period 周期
     * @return 定时任务
     */
    @Override
    PeriodicBotTask scheduleWithFixedDelay(Runnable action, long period);
    
    /**
     * 执行一个定时任务，任务之间有相同的间隔
     *
     * @param action   动作
     * @param period   周期
     * @param timeUnit 时间单位
     * @return 定时任务
     */
    @Override
    PeriodicBotTask scheduleWithFixedDelay(Runnable action, long period, TimeUnit timeUnit);
    
    /**
     * 执行一个定时任务，任务之间严格按照周期调度
     *
     * @param action 动作
     * @param period 周期
     * @return 定时任务
     */
    @Override
    PeriodicBotTask scheduleAtFixedRate(Runnable action, long period);
    
    /**
     * 执行一个定时任务，任务之间严格按照周期调度
     *
     * @param action   动作
     * @param period   周期
     * @param timeUnit 时间单位
     * @return 定时任务
     */
    @Override
    PeriodicBotTask scheduleAtFixedRate(Runnable action, long period, TimeUnit timeUnit);
}
