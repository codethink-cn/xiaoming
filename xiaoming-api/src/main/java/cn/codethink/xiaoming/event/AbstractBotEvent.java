package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import lombok.Data;

/**
 * @see cn.codethink.xiaoming.event.Event
 * @author Chuanwise
 */
@Data
public class AbstractBotEvent
    extends AbstractEvent
    implements BotObject {
    
    private final Bot bot;
    
    public AbstractBotEvent(Bot bot) {
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
}
