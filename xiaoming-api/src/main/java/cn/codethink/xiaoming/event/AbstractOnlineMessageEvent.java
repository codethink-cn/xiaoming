package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.contact.Sender;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import lombok.Data;

/**
 * 抽象在线消息事件
 *
 * @author Chuanwise
 */
@Data
public abstract class AbstractOnlineMessageEvent
    extends AbstractEvent
    implements OnlineMessageEvent {
    
    private final Sender sender;
    
    private final CompoundMessage message;
    
    private final ContactOrBot target;
    
    private final long timestamp;
    
    public AbstractOnlineMessageEvent(Sender sender, CompoundMessage message, ContactOrBot target, long timestamp) {
        Preconditions.objectNonNull(sender, "sender");
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(target, "target");
        
        this.sender = sender;
        this.message = message;
        this.target = target;
        this.timestamp = timestamp;
    }
    
    @Override
    public Bot getBot() {
        return sender.getBot();
    }
}
