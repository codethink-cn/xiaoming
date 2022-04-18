package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.BotNameChangedEvent
 */
@Data
@SuppressWarnings("all")
public class BotNameChangedEventImpl
    extends AbstractBotEvent
    implements BotNameChangedEvent {
    
    private final String previousName;
    
    private final String currentName;
    
    public BotNameChangedEventImpl(Bot bot, String previousName, String currentName) {
        super(bot);
    
        Preconditions.objectArgumentNonEmpty(previousName, "previous name");
        Preconditions.objectArgumentNonEmpty(currentName, "current name");
        
        this.previousName = previousName;
        this.currentName = currentName;
    }
}
