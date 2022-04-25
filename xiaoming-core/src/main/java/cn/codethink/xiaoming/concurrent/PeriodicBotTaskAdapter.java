package cn.codethink.xiaoming.concurrent;

import cn.chuanwise.common.concurrent.PeriodicTask;
import cn.chuanwise.common.concurrent.PeriodicTaskAdapter;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

import java.util.concurrent.Future;

@Getter
public class PeriodicBotTaskAdapter
    extends PeriodicTaskAdapter
    implements PeriodicBotTask {
    
    private final Bot bot;
    
    public PeriodicBotTaskAdapter(Bot bot, PeriodicTask task) {
        super(task);
    
        Preconditions.objectNonNull(bot, "bot");
        
        this.bot = bot;
    }
}
