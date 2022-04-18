package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.contact.Sender;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * 和在线消息相关的事件
 *
 * @author Chuanwise
 */
public interface OnlineMessageEvent
    extends MessageEvent {
    
    /**
     * 获取收到的消息。
     *
     * 如果是在线消息，必定有消息源，因此是具备元数据的消息，则必然是复合消息。
     *
     * @return 复合消息
     */
    @Override
    CompoundMessage getMessage();
    
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
