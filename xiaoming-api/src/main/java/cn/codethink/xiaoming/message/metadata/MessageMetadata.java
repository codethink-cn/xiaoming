package cn.codethink.xiaoming.message.metadata;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.basic.Text;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessageBuilder;
import cn.codethink.xiaoming.property.Property;

/**
 * 元数据消息，如引用回复 {@link Quote}、消息源 {@link MessageSource} 等信息。
 * 消息元数据将存在于复合消息 {@link cn.codethink.xiaoming.message.compound.CompoundMessage} 中的元数据表中。
 *
 * 通过元数据类型 {@link MessageMetadataType} 获取消息元数据。
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public interface MessageMetadata
    extends Serializable {
    
    /**
     * 元数据类型
     *
     * @return 元数据类型
     */
    Property<?> getMetadataType();
    
    /**
     * 在该消息结尾添加一些复合消息
     *
     * @param compoundMessage 追加的复合消息
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(CompoundMessage compoundMessage) {
        return plus((Iterable<? extends Message>) compoundMessage);
    }
    
    /**
     * 在该消息结尾添加一个新的消息
     *
     * @param message 追加的消息
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(Message message) {
        Preconditions.objectNonNull(message, "message");
    
        return CompoundMessageBuilder.newInstance()
            .plus(this)
            .plus(message)
            .build();
    }
    
    /**
     * 在该消息结尾添加一些新的文本
     *
     * @param text 追加的文本
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(CharSequence text) {
        Preconditions.objectNonNull(text, "text");
        Preconditions.argument(text.length() > 0, "text is empty");
        
        return plus(Text.of(text.toString()));
    }
    
    /**
     * 在该消息结尾添加一些新的文本
     *
     * @param text 追加的文本
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
        
        return CompoundMessageBuilder.newInstance()
            .plus(this)
            .plus(text)
            .build();
    }
    
    /**
     * 为来消息添加一个元数据
     *
     * @param messageMetadata 消息元数据
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(MessageMetadata messageMetadata) {
        Preconditions.objectNonNull(messageMetadata, "message metadata");
    
        return CompoundMessageBuilder.newInstance()
            .plus(this)
            .plus(messageMetadata)
            .build();
    }
    
    /**
     * 在该消息结尾添加一些新的消息
     *
     * @param messages 追加的消息
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(Message... messages) {
        Preconditions.objectNonNull(messages, "messages");
    
        return CompoundMessageBuilder.newInstance()
            .plus(this)
            .plus(messages)
            .build();
    }
    
    /**
     * 在该消息结尾添加一些新的消息
     *
     * @param iterable 追加的消息的迭代器
     * @return 连接形成的复合消息
     */
    default CompoundMessage plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        return CompoundMessageBuilder.newInstance()
            .plus(this)
            .plus(iterable)
            .build();
    }
}
