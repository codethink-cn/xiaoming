package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.BotStopEvent
 */
public class BotStopEventImpl
    extends AbstractBotEvent
    implements BotStopEvent {
    
    public BotStopEventImpl(Bot bot) {
        super(bot);
    }
}
