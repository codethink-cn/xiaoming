package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;

/**
 * 已发送消息事件
 *
 * @author Chuanwise
 */
public interface PostSendMessageEvent
    extends SendMessageEvent, OnlineMessageEvent, MessageReceipt {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    @Override
    default Bot getSender() {
        return getBot();
    }
}