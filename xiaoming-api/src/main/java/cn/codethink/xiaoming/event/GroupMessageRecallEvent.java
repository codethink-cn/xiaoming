package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupSender;
import cn.codethink.xiaoming.contact.MassSender;

/**
 * 群消息撤回事件
 *
 * @author Chuanwise
 */
public interface GroupMessageRecallEvent
    extends MassMessageRecallEvent, GroupEvent {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    GroupSender getSender();
    
    /**
     * 获取目标会话
     *
     * @return 目标会话
     */
    @Override
    Group getTarget();
}
