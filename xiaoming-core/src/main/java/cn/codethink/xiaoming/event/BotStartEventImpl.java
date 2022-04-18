package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;

/**
 * @author Chuanwise
 *
 * @see BotStartEvent
 */
public class BotStartEventImpl
    extends AbstractBotEvent
    implements BotStartEvent {
    
    public BotStartEventImpl(Bot bot) {
        super(bot);
    }
}
