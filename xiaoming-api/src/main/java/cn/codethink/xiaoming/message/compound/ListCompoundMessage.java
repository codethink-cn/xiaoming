package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.element.BasicMessage;
import cn.codethink.xiaoming.message.element.MessageMetadataType;

import java.util.*;

/**
 * 列表实现的复合消息。
 * 不论列表是否是不可变的列表，调用 {@link #plus(String)} 类方法时，都将触发赋值新列表的行为。
 * 元数据表同理。
 *
 * @author Chuanwise
 */
public class ListCompoundMessage
    extends AbstractCompoundMessage
    implements CompoundMessage {
    
    private final Map<MessageMetadataType<?>, Object> metadata;
    
    private final List<BasicMessage> basicMessages;
    
    public ListCompoundMessage(List<BasicMessage> basicMessages, Map<MessageMetadataType<?>, Object> metadata) {
        Preconditions.objectArgumentNonEmpty(basicMessages, "basic messages");
        Preconditions.objectNonNull(metadata, "metadata");
    
        this.basicMessages = basicMessages;
        this.metadata = metadata;
    }
    
    @Override
    public CompoundMessage plus(Message message) {
        Preconditions.objectNonNull(message, "message");
    
        return new SimpleCompoundMessageBuilder(this)
            .plus(message)
            .build();
    }
    
    @Override
    public CompoundMessage plus(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
    
        return new SimpleCompoundMessageBuilder(this)
            .plus(text)
            .build();
    }
    
    @Override
    public CompoundMessage plus(Message... messages) {
        Preconditions.objectNonNull(messages, "messages");
    
        if (Arrays.isEmpty(messages)) {
            return this;
        }
    
        return new SimpleCompoundMessageBuilder(this)
            .plus(messages)
            .build();
    }
    
    @Override
    public CompoundMessage plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        return new SimpleCompoundMessageBuilder(this)
            .plus(iterable)
            .build();
    }
    
    @Override
    public int size() {
        return basicMessages.size();
    }
    
    @Override
    @SuppressWarnings("all")
    public <T> T getMetadata(MessageMetadataType<T> type) {
        Preconditions.objectNonNull(type, "type");
        return (T) metadata.get(type);
    }
    
    @Override
    public Map<MessageMetadataType<?>, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    @Override
    public boolean isEmpty() {
        return basicMessages.isEmpty();
    }
    
    @Override
    public Iterator<BasicMessage> iterator() {
        return basicMessages.iterator();
    }
}
