package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.SerializableMessage;
import cn.codethink.xiaoming.message.SummarizableMessage;
import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * 基础是消息不可再分的各个组成部分，如文本 {@link Text} 和 At {@link At}。
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public interface BasicMessage
    extends SerializableMessage, SummarizableMessage {
    
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
}