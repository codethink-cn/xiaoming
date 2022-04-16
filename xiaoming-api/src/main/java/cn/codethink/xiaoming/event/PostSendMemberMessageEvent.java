package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Member;

/**
 * 已发送集体成员消息
 *
 * @author Chuanwise
 */
public interface PostSendMemberMessageEvent
    extends PostSendMessageEvent, MassEvent {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Member getTarget();
}
