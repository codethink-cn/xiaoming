package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Member;

/**
 * 来自集体的消息源
 *
 * @author Chuanwise
 */
public interface FromMassMessageReference
    extends IncomingOnlineMessageReference, MassMessageReference {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    Member getSender();
}
