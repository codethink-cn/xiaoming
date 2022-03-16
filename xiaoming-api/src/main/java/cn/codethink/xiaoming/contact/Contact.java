package cn.codethink.xiaoming.contact;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.content.MessageContent;
import cn.codethink.xiaoming.concurrent.BotFuture;

/**
 * 可以发送消息的地方
 * 
 * @author Chuanwise
 */
public interface Contact
        extends BotObject {
    
    /**
     * 获取编号
     *
     * @return 编号
     */
    Code getCode();
    
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
