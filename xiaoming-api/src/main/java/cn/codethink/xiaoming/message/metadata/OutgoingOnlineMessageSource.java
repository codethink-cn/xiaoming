package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.Bot;

/**
 * 出站消息源，表示 Bot 发送的一条消息
 *
 * @author Chuanwise
 */
public interface OutgoingOnlineMessageSource
    extends OnlineMessageSource {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    Bot getSender();
}
