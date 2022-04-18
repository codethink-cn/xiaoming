package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

@Data
@SuppressWarnings("all")
public class BotAvatarChangedEventImpl
    extends AbstractBotEvent
    implements BotAvatarChangedEvent {
    
    private final String previousAvatarUrl;
    
    private final String currentAvatarUrl;
    
    public BotAvatarChangedEventImpl(Bot bot, String previousAvatarUrl, String currentAvatarUrl) {
        super(bot);
    
        Preconditions.objectArgumentNonEmpty(previousAvatarUrl, "previous avatar url");
        Preconditions.objectArgumentNonEmpty(currentAvatarUrl, "current avatar url");
        
        this.previousAvatarUrl = previousAvatarUrl;
        this.currentAvatarUrl = currentAvatarUrl;
    }
}
