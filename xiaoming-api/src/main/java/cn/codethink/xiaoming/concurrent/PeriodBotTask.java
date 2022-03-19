package cn.codethink.xiaoming.concurrent;

/**
 * 周期性任务
 *
 * @see cn.codethink.xiaoming.concurrent.BotTask
 * @author Chuanwise
 */
public interface PeriodBotTask
        extends BotTask {
    
    /**
     * 只跳过下一次执行
     *
     * @return 是否跳过
     */
    boolean skip();
    
    /**
     * 判断是否正在跳过下一次执行
     *
     * @return 是否正在跳过下一次执行
     */
    boolean isSkipping();
}
