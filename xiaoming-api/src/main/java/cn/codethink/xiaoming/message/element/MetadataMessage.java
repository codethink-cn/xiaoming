package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.SerializableMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.compound.SingletonCompoundMessage;
import cn.codethink.xiaoming.message.reference.MessageReference;

/**
 * 元数据消息，如引用回复 {@link Quote}、消息源 {@link MessageReference} 等信息。
 * 消息元数据将存在于复合消息 {@link cn.codethink.xiaoming.message.compound.CompoundMessage} 中的元数据表中。
 *
 * 通过元数据类型 {@link MessageMetadataType} 获取消息元数据。
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public interface MetadataMessage
    extends Message, SerializableMessage {
    
    /**
     * 元数据类型
     *
     * @return 元数据类型
     */
    MessageMetadataType<?> getMetadataType();
    
    @Override
    default CompoundMessage plus(Message message) {
        return asCompoundMessage().plus(message);
    }
    
    @Override
    default CompoundMessage plus(String text) {
        return asCompoundMessage().plus(text);
    }
    
    @Override
    default CompoundMessage plus(Message... messages) {
        return asCompoundMessage().plus(messages);
    }
    
    @Override
    default CompoundMessage plus(Iterable<? extends Message> iterable) {
        return asCompoundMessage().plus(iterable);
    }
    
    @Override
    default CompoundMessage asCompoundMessage() {
        return new SingletonCompoundMessage(this);
    }
}
