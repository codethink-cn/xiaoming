package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.element.Text;

/**
 * 复合消息构建器
 *
 * @author Chuanwise
 */
public interface CompoundMessageBuilder
    extends CompoundMessage {
    
    @Override
    CompoundMessageBuilder plus(String text);
    
    @Override
    default CompoundMessageBuilder plus(CharSequence text) {
        Preconditions.objectNonNull(text, "text");
        Preconditions.argument(text.length() > 0, "text is empty");
    
        return plus(new Text(text.toString()));
    }
    
    @Override
    CompoundMessageBuilder plus(Message message);
    
    @Override
    CompoundMessageBuilder plus(Message... messages);
    
    @Override
    CompoundMessageBuilder plus(Iterable<? extends Message> iterable);
    
    /**
     * 创建一个新的消息构建器
     *
     * @return 消息构建器
     */
    static CompoundMessageBuilder builder() {
        return new SimpleCompoundMessageBuilder();
    }
    
    /**
     * 创建一个新的消息构建器，并预留一定的大小
     *
     * @param capacity 预留大小
     * @return 消息构建器
     */
    static CompoundMessageBuilder reserve(int capacity) {
        return new SimpleCompoundMessageBuilder(capacity);
    }
    
    /**
     * 通过复制现有的复合消息创建消息构建器
     *
     * @param compoundMessage 复合消息
     * @return 消息构建器
     */
    static CompoundMessageBuilder copy(CompoundMessage compoundMessage) {
        return new SimpleCompoundMessageBuilder(compoundMessage);
    }
    
    /**
     * 通过现有的复合消息，创建惰性消息构建器。
     *
     * @param compoundMessage 复合消息
     * @return 消息构建器
     * @see LazyCompoundMessageBuilder
     */
    static CompoundMessageBuilder lazy(CompoundMessage compoundMessage) {
        Preconditions.objectNonNull(compoundMessage, "compound message");
        
        return new LazyCompoundMessageBuilder(compoundMessage);
    }
    
    /**
     * 构建一个复合消息
     *
     * @return 复合消息
     */
    CompoundMessage build();
}
