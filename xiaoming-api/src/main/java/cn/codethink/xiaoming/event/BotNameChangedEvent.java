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
    extends AbstractBotEvent {
    
    /**
     * 修改前的名称
     */
    private final String previousName;
    
    /**
     * 修改后的名称
     */
    private final String currentName;
    
    public BotNameChangedEvent(Bot bot, String previousName, String currentName) {
        super(bot);
    
        Preconditions.objectArgumentNonEmpty(previousName, "previous name");
        Preconditions.objectArgumentNonEmpty(currentName, "current name");
        
        this.previousName = previousName;
        this.currentName = currentName;
    }
}
