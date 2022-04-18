package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * @author Chuanwise
 *
 * @see ReceiveGroupMemberMessageEvent
 */
public class ReceiveGroupMemberMessageEventImpl
    extends AbstractOnlineMessageEvent
    implements ReceiveGroupMemberMessageEvent {
    
    public ReceiveGroupMemberMessageEventImpl(GroupMember sender, CompoundMessage message, long timestamp) {
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
