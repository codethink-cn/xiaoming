package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see PreSendGroupMessageEvent
 */
@Data
public class PreSendGroupMessageEventImpl
    extends AbstractCancellableEvent
    implements PreSendGroupMessageEvent {
    
    private final Group group;
    
    private Message message;
    
    public PreSendGroupMessageEventImpl(Group group, Message message) {
        Preconditions.objectNonNull(group, "group");
        Preconditions.objectNonNull(message, "message");
        
        this.group = group;
        this.message = message;
    }
    
    @Override
    public void setMessage(Message message) {
        Preconditions.objectNonNull(message, "message");
        
        this.message = message;
    }
    
    @Override
    public Bot getBot() {
        return group.getBot();
    }
    
    @Override
    public Group getMass() {
        return group;
    }
    
    @Override
    public Group getTarget() {
        return getMass();
    }
}
