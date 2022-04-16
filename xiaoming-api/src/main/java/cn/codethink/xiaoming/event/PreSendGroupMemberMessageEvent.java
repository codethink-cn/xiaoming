package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.contact.Member;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 即将发送群私聊消息事件
 *
 * @author Chuanwise
 */
@Data
public class PreSendGroupMemberMessageEvent
    extends AbstractCancellableEvent
    implements PreSendMemberMessageEvent, GroupEvent {
    
    private final GroupMember member;
    
    private final Message message;
    
    public PreSendGroupMemberMessageEvent(GroupMember member, Message message) {
        Preconditions.objectNonNull(member, "member");
        Preconditions.objectNonNull(message, "message");
        
        this.member = member;
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
