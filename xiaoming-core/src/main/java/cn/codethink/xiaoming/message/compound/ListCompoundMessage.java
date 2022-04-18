package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.property.Property;

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
    
    private final Map<Property<?>, MessageMetadata> metadata;
    
    private final List<BasicMessage> basicMessages;
    
    public ListCompoundMessage(List<BasicMessage> basicMessages, Map<Property<?>, MessageMetadata> metadata) {
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
    public CompoundMessage plus(MessageMetadata messageMetadata) {
        Preconditions.objectNonNull(messageMetadata, "message metadata");
    
        return new SimpleCompoundMessageBuilder(this)
            .plus(messageMetadata)
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
    public <T extends MessageMetadata> T getMetadata(Property<T> type) {
        Preconditions.objectNonNull(type, "type");
        return (T) metadata.get(type);
    }
    
    @Override
    public Map<Property<?>, MessageMetadata> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    @Override
    public boolean containsMetadata(Property<?> type) {
        Preconditions.objectNonNull(type, "type");

        return metadata.containsKey(type);
    }
    
    @Override
    @SuppressWarnings("all")
    public <T extends MessageMetadata> T getMetadataOrFail(Property<T> type) {
        Preconditions.objectNonNull(type, "type");
        return (T) Maps.getOrFail(metadata, type);
    }
    
    @Override
    public BasicMessage get(int index) {
        Preconditions.objectIndex(index, basicMessages.size(), "basic message");
        return basicMessages.get(index);
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
