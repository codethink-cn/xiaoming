package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;

/**
 * 某种可以用来发送消息的对象
 *
 * @author Chuanwise
 */
public interface Contact
    extends ContactOrBot, Cached {
    
    /**
     * 发送一个消息
     *
     * @param message 消息内容
     * @return 消息回执
     * @throws cn.codethink.xiaoming.event.CancelledException 对应的发送消息事件 {@link cn.codethink.xiaoming.event.PostSendMessageEvent}
     *                                                        被取消（{@link cn.codethink.xiaoming.event.CancellableEvent#cancel(boolean)}）时
     */
    MessageReceipt sendMessage(Message message);
    
    /**
     * 发送一个消息
     *
     * @param messageCode 消息码
     * @return 消息发送 Future
     */
    default MessageReceipt sendMessage(String messageCode) {
        Preconditions.objectArgumentNonEmpty(messageCode, "message code");
        
        return sendMessage(MessageCode.deserializeToCompoundMessage(messageCode, getBot()));
    }
    
    // TODO: 2022/4/16 add nextMessage, nextFlap, nextEvent(), nextEvent(ContactEvent.class), nextCompoundMessage and etc.
}