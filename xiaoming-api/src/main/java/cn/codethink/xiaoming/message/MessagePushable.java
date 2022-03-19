package cn.codethink.xiaoming.message;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.concurrent.BotFuture;
import cn.codethink.xiaoming.message.content.MessageContent;

/**
 * 某种可以发送消息的组件
 *
 * @author Chuanwise
 */
public interface MessagePushable {
    
    /**
     * 发送一个消息
     *
     * @param messageContent 消息内容
     * @return 消息发送 Future
     */
    BotFuture<Message> sendMessage(MessageContent messageContent);
    
    /**
     * 发送一个消息
     *
     * @param messageCode 消息码
     * @return 消息发送 Future
     */
    default BotFuture<Message> sendMessage(String messageCode) {
        Preconditions.namedArgumentNonEmpty(messageCode, "message code");
        
        return sendMessage(MessageCode.deserialize(messageCode));
    }
}
