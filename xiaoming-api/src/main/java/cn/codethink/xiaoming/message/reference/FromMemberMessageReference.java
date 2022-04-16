package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.contact.Member;

/**
 * 来自集体成员的消息源
 *
 * @author Chuanwise
 */
public interface FromMemberMessageReference
    extends IncomingOnlineMessageReference, MassMessageReference {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    Member getSender();
}
