package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.contact.Stranger;

/**
 * 发送到陌生人的消息源
 *
 * @author Chuanwise
 */
public interface ToStrangerMessageSource
    extends OutgoingOnlineMessageSource {
    
    /**
     * 获取消息接收方
     *
     * @return 消息接收方
     */
    @Override
    Stranger getTarget();
}
