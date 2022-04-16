package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.contact.Member;

/**
 * 发送到临时会话的消息引用
 *
 * @author Chuanwise
 */
public interface ToMemberMessageReference
    extends OutgoingOnlineMessageReference, MassMessageReference {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Member getTarget();
}
