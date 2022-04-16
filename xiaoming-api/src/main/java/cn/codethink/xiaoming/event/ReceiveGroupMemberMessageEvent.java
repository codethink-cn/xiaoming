package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.contact.Sender;
import cn.codethink.xiaoming.message.Message;

/**
 * 收到群临时会话消息事件
 *
 * @author Chuanwise
 */
public class ReceiveGroupMemberMessageEvent
    extends AbstractOnlineMessageEvent
    implements ReceiveMemberMessageEvent {
    
    public ReceiveGroupMemberMessageEvent(GroupMember sender, Message message, long timestamp) {
        super(sender, message, sender.getBot(), timestamp);
    }
    
    @Override
    public GroupMember getSender() {
        return (GroupMember) super.getSender();
    }
    
    @Override
    public Group getTarget() {
        return (Group) super.getTarget();
    }
}
