package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
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
        extends AbstractBotEvent {
    
    /**
     * 修改前的头像
     */
    private final String previousAvatarUrl;
    
    /**
     * 修改后的头像
     */
    private final String currentAvatarUrl;
    
    public BotAvatarChangedEvent(Bot bot, String previousAvatarUrl, String currentAvatarUrl) {
        super(bot);
    
        Preconditions.objectArgumentNonEmpty(previousAvatarUrl, "previous avatar url");
        Preconditions.objectArgumentNonEmpty(currentAvatarUrl, "current avatar url");
        
        this.previousAvatarUrl = previousAvatarUrl;
        this.currentAvatarUrl = currentAvatarUrl;
    }
}
