package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Stranger;

/**
 * 来自好友的消息源
 *
 * @author Chuanwise
 */
public interface FromStrangerMessageReference
    extends IncomingOnlineMessageReference {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    Stranger getSender();
}
