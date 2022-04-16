package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 即将发送群消息事件
 *
 * @author Chuanwise
 */
@Data
public class PreSendGroupMessageEvent
    extends AbstractCancellableEvent
    implements PreSendMassMessageEvent, GroupEvent {
    
    private final Group group;
    
    private final Message message;
    
    public PreSendGroupMessageEvent(Group group, Message message) {
        Preconditions.objectNonNull(group, "group");
        Preconditions.objectNonNull(message, "message");
        
        this.group = group;
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
