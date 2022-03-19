package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.message.content.MessageContent;

/**
 * 消息编解码器
 *
 * @author Chuanwise
 */
public interface MessageContentSerializer<T> {
    
    /**
     * 反序列化消息码
     *
     * @param message 输入消息
     * @return 消息内容
     */
    MessageContent deserialize(T message);
    
    /**
     * 序列化消息码
     *
     * @param content 消息内容
     * @return 消息码
     */
    T serialize(MessageContent content);
}
