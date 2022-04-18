package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.message.receipt.MessageReceipt;

/**
 * 已发送好友消息事件
 *
 * @author Chuanwise
 */
public interface PostSendStrangerMessageEvent
    extends PostSendMessageEvent, StrangerEvent, MessageReceipt {
}
