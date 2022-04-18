package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.ReceiveGroupMessageEvent
 */
public class ReceiveGroupMessageEventImpl
    extends AbstractOnlineMessageEvent
    implements ReceiveGroupMessageEvent {
    
    public ReceiveGroupMessageEventImpl(GroupSender sender, CompoundMessage message, long timestamp) {
        super(sender, message, sender.getMass(), timestamp);
    }
    
    @Override
    public GroupSender getSender() {
        return (GroupSender) super.getSender();
    }
    
    @Override
    public Group getTarget() {
        return (Group) super.getTarget();
    }
    
    @Override
    public Group getMass() {
        return getTarget();
    }
}
