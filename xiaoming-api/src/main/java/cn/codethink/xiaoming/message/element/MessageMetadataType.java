package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.reference.MessageReference;

/**
 * 消息元数据类型
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public class MessageMetadataType<T> {
    
    /**
     * 引用回复
     */
    public static final MessageMetadataType<Quote> QUOTE = new MessageMetadataType<>();
    
    /**
     * 消息源
     */
    public static final MessageMetadataType<MessageReference> REFERENCE = new MessageMetadataType<>();
    
    private MessageMetadataType() {
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
