package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.element.MessageElement;

/**
 * 消息元素编解码器
 *
 * @author Chuanwise
 */
public interface MessageElementCodec<T extends MessageElement> {
    
    /**
     * 解码对应的消息
     *
     * @param messageCode 消息码
     * @return 当解析失败返回 null
     */
    T decode(String messageCode);
    
    /**
     * 编码对应的消息
     *
     * @param messageElement 消息元素
     * @return 编码后的消息
     */
    String encode(T messageElement);
}
