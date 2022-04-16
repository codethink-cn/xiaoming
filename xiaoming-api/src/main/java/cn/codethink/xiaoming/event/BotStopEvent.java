package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.AbstractBotObject;

/**
 * 机器人关闭时发出
 *
 * @author Chuanwise
 */
public class BotStopEvent
        extends AbstractBotEvent {
    
    public BotStopEvent(Bot bot) {
        super(bot);
    }
}
