package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.BotOfflineEvent
 */
@Data
@SuppressWarnings("all")
public class BotOfflineEventImpl
    extends AbstractBotEvent
    implements BotOfflineEvent {
    
    /**
     * 是否需要重新登录 Bot
     */
    private final boolean relogin;
    
    public BotOfflineEventImpl(Bot bot, boolean relogin) {
        super(bot);
        
        this.relogin = relogin;
    }
}
