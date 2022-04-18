package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Group;

/**
 * 即将发送群私聊消息事件
 *
 * @author Chuanwise
 */
public interface PreSendGroupMemberMessageEvent
    extends PreSendMemberMessageEvent, GroupEvent {
    
    /**
     * 获取群
     *
     * @return 群
     */
    @Override
    Group getMass();
}
