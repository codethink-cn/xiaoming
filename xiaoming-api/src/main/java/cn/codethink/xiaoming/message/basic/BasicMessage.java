package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.Summarizable;
import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * 基础是消息不可再分的各个组成部分，如文本 {@link Text} 和 At {@link At}。
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public interface BasicMessage
    extends Message, Serializable, Summarizable {
    
    @Override
    default CompoundMessage plus(Message message) {
        Preconditions.objectNonNull(message, "message");
        
        return asCompoundMessage().plus(message);
    }
    
    @Override
    default CompoundMessage plus(String text) {
        Preconditions.objectNonNull(text, "text");

        return asCompoundMessage().plus(text);
    }
    
    @Override
    default CompoundMessage plus(MessageMetadata messageMetadata) {
        Preconditions.objectNonNull(messageMetadata, "message metadata");

        return asCompoundMessage().plus(messageMetadata);
    }
    
    @Override
    default CompoundMessage plus(Message... messages) {
        Preconditions.objectNonNull(messages, "messages");
    
        return asCompoundMessage().plus(messages);
    }
    
    @Override
    default CompoundMessage plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");

        return asCompoundMessage().plus(iterable);
    }
}