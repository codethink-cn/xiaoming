package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.Message;

/**
 * 收到群消息事件
 *
 * @author Chuanwise
 */
public class ReceiveGroupMessageEvent
    extends AbstractOnlineMessageEvent
    implements ReceiveMassMessageEvent {
    
    public ReceiveGroupMessageEvent(GroupSender sender, Message message, long timestamp) {
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
}
