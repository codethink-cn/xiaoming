package cn.codethink.xiaoming.concurrent;

/**
 * 异步监听器
 *
 * @author Chuanwise
 */
@FunctionalInterface
public interface BotListener {
    
    /**
     * 执行完成时的回调方法
     *
     * @param botFuture future
     */
    void operationComplete(BotTask botFuture);
}
