package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Member;

/**
 * 即将发送成员私聊消息事件
 *
 * @author Chuanwise
 */
public interface PreSendMemberMessageEvent
    extends PreSendMessageEvent, MemberEvent {
    
    /**
     * 获取发送目标成员
     *
     * @return 发送目标成员
     */
    @Override
    Member getTarget();
}
