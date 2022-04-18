package cn.codethink.xiaoming.event;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.ToGroupMemberMessageSource;
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
    
    private final CompoundMessage message;
    
    private final ToGroupMemberMessageSource messageSource;
    
    public PostSendGroupMemberMessageEvent(GroupMember member, CompoundMessage message, ToGroupMemberMessageSource messageSource) {
        Preconditions.objectNonNull(member, "member");
        Preconditions.objectNonNull(message, "message");
        
        this.member = member;
        this.message = message;
        this.messageSource = messageSource;
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
    public CompoundMessage getMessage() {
        return message;
    }
    
    @Override
    public long getTimestamp() {
        return messageSource.getTimestamp();
    }
    
    @Override
    public GroupMember getTarget() {
        return member;
    }
}
