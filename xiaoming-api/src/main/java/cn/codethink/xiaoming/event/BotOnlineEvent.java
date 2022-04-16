package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * Bot 登录事件
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class BotOnlineEvent
        extends AbstractBotEvent {
    
    public BotOnlineEvent(Bot bot) {
        super(bot);
    }
}
