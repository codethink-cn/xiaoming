package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.contact.Sender;

/**
 * 和在线消息相关的事件
 *
 * @author Chuanwise
 */
public interface OnlineMessageEvent
    extends MessageEvent {
    
    /**
     * 获取消息发送方
     *
     * @return 消息发送方
     */
    Sender getSender();
    
    /**
     * 获取目标会话
     *
     * @return 目标会话
     */
    ContactOrBot getTarget();
    
    /**
     * 获取消息时间戳
     *
     * @return 消息时间戳
     */
    long getTimestamp();
}
