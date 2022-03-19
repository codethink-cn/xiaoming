package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * Bot 修改昵称
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class BotNameChangedEvent
        extends AbstractBotObject {
    
    private final String previousName;
    
    private final String currentName;
    
    public BotNameChangedEvent(Bot bot, String previousName, String currentName) {
        super(bot);
    
        Preconditions.namedArgumentNonEmpty(previousName, "previous name");
        Preconditions.namedArgumentNonEmpty(currentName, "current name");
        
        this.previousName = previousName;
        this.currentName = currentName;
    }
}
