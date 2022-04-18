package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupSender;

/**
 * 收到群消息事件
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public interface ReceiveGroupMessageEvent
    extends ReceiveMassMessageEvent, GroupEvent {
    
    @Override
    GroupSender getSender();
    
    @Override
    Group getTarget();
    
    @Override
    Group getMass();
}
