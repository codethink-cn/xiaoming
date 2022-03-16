package cn.codethink.xiaoming.event;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * Bot 修改头像
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class BotAvatarChangedEvent
        extends AbstractBotObject {
    
    private final String previousAvatarUrl;
    
    private final String currentAvatarUrl;
    
    public BotAvatarChangedEvent(Bot bot, String previousAvatarUrl, String currentAvatarUrl) {
        super(bot);
    
        Preconditions.namedArgumentNonEmpty(previousAvatarUrl, "previous avatar url");
        Preconditions.namedArgumentNonEmpty(currentAvatarUrl, "current avatar url");
        
        this.previousAvatarUrl = previousAvatarUrl;
        this.currentAvatarUrl = currentAvatarUrl;
    }
}
