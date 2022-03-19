package cn.codethink.xiaoming.concurrent;

/**
 * 异步监听器
 *
 * @author Chuanwise
 */
@FunctionalInterface
public interface BotTaskListener {
    
    /**
     * 任务结束后关闭机器人
     */
    BotTaskListener STOP = botTask -> botTask.getBot().stop();
    
    /**
     * 任务失败后关闭机器人
     */
    BotTaskListener STOP_ON_FAILED = botTask -> {
        if (botTask.isFailed()) {
            botTask.getBot().stop();
        }
    };
    
    /**
     * 任务成功后关闭机器人
     */
    BotTaskListener STOP_ON_SUCCEED = botTask -> {
        if (botTask.isSucceed()) {
            botTask.getBot().stop();
        }
    };
    
    /**
     * 执行完成时的回调方法
     *
     * @param botTask future
     */
    void listen(BotTask botTask);
}
