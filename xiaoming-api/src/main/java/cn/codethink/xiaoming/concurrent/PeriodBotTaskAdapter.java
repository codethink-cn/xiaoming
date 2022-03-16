package cn.codethink.xiaoming.concurrent;

/**
 * 周期性 BotTask 适配器
 *
 * @see cn.codethink.xiaoming.concurrent.PeriodBotTask
 * @author Chuanwise
 */
public class PeriodBotTaskAdapter
        extends BotTaskAdapter
        implements PeriodBotTask {
    
    public PeriodBotTaskAdapter(PeriodBotTask task) {
        super(task);
    }
    
    @Override
    public boolean skip() {
        return ((PeriodBotTask) task).skip();
    }
    
    @Override
    public boolean isSkipping() {
        return ((PeriodBotTask) task).isSkipping();
    }
}
