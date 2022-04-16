package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

/**
 * 机器人启动时发出
 *
 * @author Chuanwise
 */
public class BotStartEvent
        extends AbstractBotEvent {
    
    public BotStartEvent(Bot bot) {
        super(bot);
    }
}
