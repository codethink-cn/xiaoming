package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Member;

/**
 * 发送到临时会话的消息源
 *
 * @author Chuanwise
 */
public interface ToMemberMessageSource
    extends OutgoingOnlineMessageSource, MassMessageSource {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Member getTarget();
}
