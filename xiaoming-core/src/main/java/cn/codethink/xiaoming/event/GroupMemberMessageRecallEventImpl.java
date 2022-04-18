package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.GroupMemberMessageRecallEvent
 */
@Data
public class GroupMemberMessageRecallEventImpl
    extends AbstractEvent
    implements GroupMemberMessageRecallEvent {
    
    private final CompoundMessage message;
    
    private final long timestamp;
    
    private final GroupMember member;
    
    public GroupMemberMessageRecallEventImpl(GroupMember member, CompoundMessage message, long timestamp) {
        
        Preconditions.objectNonNull(member, "friend");
        Preconditions.objectNonNull(timestamp, "timestamp");
        
        this.member = member;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    @Override
    public Bot getBot() {
        return member.getBot();
    }
    
    @Override
    public GroupMember getSender() {
        return member;
    }
    
    @Override
    public GroupMember getTarget() {
        return member;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public Group getMass() {
        return member.getMass();
    }
}
