package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class BotOnlineEventImpl
    extends AbstractBotEvent
    implements BotOnlineEvent {
    
    public BotOnlineEventImpl(Bot bot) {
        super(bot);
    }
}
