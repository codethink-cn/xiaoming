package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import lombok.Data;


/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.GroupMessageRecallEvent
 */
@Data
public class GroupMessageRecallEventImpl
    extends AbstractEvent
    implements GroupMessageRecallEvent {
    
    private final Group mass;
    
    private final CompoundMessage message;
    
    private final GroupSender sender;
    
    private final UserOrBot operator;
    
    private final long timestamp;
    
    public GroupMessageRecallEventImpl(Group mass, CompoundMessage message, GroupSender sender, UserOrBot operator, long timestamp) {
        
        Preconditions.objectNonNull(mass, "group");
        Preconditions.objectNonNull(sender, "sender");
        Preconditions.objectNonNull(operator, "operator");
        Preconditions.objectNonNull(timestamp, "timestamp");
        
        this.mass = mass;
        this.message = message;
        this.sender = sender;
        this.operator = operator;
        this.timestamp = timestamp;
    }
    
    @Override
    public Bot getBot() {
        return mass.getBot();
    }
    
    @Override
    public Group getTarget() {
        return mass;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
