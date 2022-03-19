package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * Bot 离线事件
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class BotOfflineEvent
        extends AbstractBotObject {
    
    private final boolean relogin;
    
    public BotOfflineEvent(Bot bot, boolean relogin) {
        super(bot);
        
        this.relogin = relogin;
    }
}
