package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * Bot 重新登录事件
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class BotReloginEvent
        extends AbstractBotObject {
    
    private final Throwable cause;
    
    public BotReloginEvent(Bot bot, Throwable cause) {
        super(bot);
        
        this.cause = cause;
    }
}
