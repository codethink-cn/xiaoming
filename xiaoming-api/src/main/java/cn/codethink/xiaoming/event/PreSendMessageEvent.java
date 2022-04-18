package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.message.Message;

/**
 * 即将发送消息事件
 *
 * @author Chuanwise
 */
public interface PreSendMessageEvent
    extends SendMessageEvent, CancellableEvent {
    
    /**
     * 修改要发送的消息内容
     *
     * @param message 新的消息内容
     * @throws NullPointerException message 为 null
     */
    void setMessage(Message message);
}
