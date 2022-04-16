package cn.codethink.xiaoming.event;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.metadata.ToGroupMemberMessageReference;
import lombok.Data;

/**
 * 已发送群临时会话消息事件
 *
 * @author Chuanwise
 */
@Data
public class PostSendGroupMemberMessageEvent
    extends AbstractEvent
    implements PostSendMemberMessageEvent {
    
    private final GroupMember member;
    
    private final Message message;
    
    private final ToGroupMemberMessageReference messageReference;
    
    public PostSendGroupMemberMessageEvent(GroupMember member, Message message, ToGroupMemberMessageReference messageReference) {
        Preconditions.objectNonNull(member, "member");
        Preconditions.objectNonNull(message, "message");
        
        this.member = member;
        this.message = message;
        this.messageReference = messageReference;
    }
    
    @Override
    public Bot getBot() {
        return member.getBot();
    }
    
    @Override
    public Group getMass() {
        return member.getMass();
    }
    
    @Override
    public Message getMessage() {
        return message;
    }
    
    @Override
    public long getTimestamp() {
        return messageReference.getTimestamp();
    }
    
    @Override
    public GroupMember getTarget() {
        return member;
    }
}
