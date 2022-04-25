package cn.codethink.xiaoming.concurrent;

import cn.chuanwise.common.concurrent.Task;
import cn.chuanwise.common.concurrent.TaskAdapter;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.concurrent.BotTask
 */
@Getter
public class BotTaskAdapter
    extends TaskAdapter
    implements BotTask {
    
    private final Bot bot;
    
    public BotTaskAdapter(Bot bot, Task task) {
        super(task);
    
        Preconditions.objectNonNull(bot, "bot");
        
        this.bot = bot;
    }
}
