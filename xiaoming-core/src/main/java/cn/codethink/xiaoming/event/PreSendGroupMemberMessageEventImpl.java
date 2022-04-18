package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.contact.Member;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.PreSendGroupMemberMessageEvent
 */
@Data
public class PreSendGroupMemberMessageEventImpl
    extends AbstractCancellableEvent
    implements PreSendGroupMemberMessageEvent {
    
    private final GroupMember member;
    
    private Message message;
    
    public PreSendGroupMemberMessageEventImpl(GroupMember member, Message message) {
        Preconditions.objectNonNull(member, "member");
        Preconditions.objectNonNull(message, "message");
        
        this.member = member;
        this.message = message;
    }
    
    @Override
    public void setMessage(Message message) {
        Preconditions.objectNonNull(message, "message");
        
        this.message = message;
    }
    
    @Override
    public Bot getBot() {
        return member.getBot();
    }
    
    @Override
    public Message getMessage() {
        return message;
    }
    
    @Override
    public Member getTarget() {
        return member;
    }
    
    @Override
    public Group getMass() {
        return member.getMass();
    }
}
