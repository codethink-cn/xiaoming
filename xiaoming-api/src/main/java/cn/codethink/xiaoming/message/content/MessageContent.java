package cn.codethink.xiaoming.message.content;

import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.content.SingletonMessageContent;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.util.Preconditions;

/**
 * 消息内容
 *
 * @author Chuanwise
 */
public interface MessageContent extends Serializable {
    
    /**
     * 单个消息元素的消息内容
     *
     * @param messageElement 消息元素
     * @return 消息内容
     */
    static MessageContent singleton(MessageElement messageElement) {
        Preconditions.namedArgumentNonNull(messageElement, "message element");
        
        return new SingletonMessageContent(messageElement);
    }
}
