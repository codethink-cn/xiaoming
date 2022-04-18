package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.property.Property;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 惰性消息构建器。并不具备消息构建功能，只是在调用 plus 之类的方法时才复制所持有的复合消息，
 * 并构建真正具备消息构建功能的 {@link SimpleCompoundMessageBuilder}。
 *
 * @author Chuanwise
 */
public class LazyCompoundMessageBuilder
    extends AbstractCompoundMessageBuilder
    implements CompoundMessageBuilder {
    
    /**
     * 所持有的复合消息
     */
    private CompoundMessage compoundMessage;
    
    /**
     * 真正的消息构建器
     */
    private CompoundMessageBuilder compoundMessageBuilder;
    
    public LazyCompoundMessageBuilder(CompoundMessage compoundMessage) {
        Preconditions.objectNonNull(compoundMessage, "compound message");
        
        this.compoundMessage = compoundMessage;
    }
    
    @Override
    public CompoundMessageBuilder plus(Message message) {
        Preconditions.objectNonNull(message, "message");
    
        ready().plus(message);
    
        return this;
    }
    
    @Override
    public CompoundMessageBuilder plus(String text) {
        Preconditions.objectNonNull(text, "text");
    
        ready().plus(text);
    
        return this;
    }
    
    @Override
    public CompoundMessageBuilder plus(MessageMetadata messageMetadata) {
        Preconditions.objectNonNull(messageMetadata, "message metadata");
        
        ready().plus(messageMetadata);
        
        return this;
    }
    
    @Override
    public CompoundMessageBuilder plus(Message... messages) {
        Preconditions.objectNonNull(messages, "messages");
    
        if (messages.length == 0) {
            return this;
        }
        
        ready().plus(messages);
    
        return this;
    }
    
    @Override
    public CompoundMessageBuilder plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        final Iterator<? extends Message> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            ready().plus(iterable);
        }
    
        return this;
    }
    
    private CompoundMessageBuilder ready() {
        if (Objects.isNull(compoundMessageBuilder)) {
            compoundMessageBuilder = new SimpleCompoundMessageBuilder(compoundMessage);
        }
        return compoundMessageBuilder;
    }
    
    @Override
    public int size() {
        if (Objects.isNull(compoundMessageBuilder)) {
            return compoundMessage.size();
        } else {
            return compoundMessageBuilder.size();
        }
    }
    
    @Override
    public <T extends MessageMetadata> T getMetadata(Property<T> type) {
        Preconditions.objectNonNull(type, "type");
    
        if (Objects.isNull(compoundMessageBuilder)) {
            return compoundMessage.getMetadata(type);
        } else {
            return compoundMessageBuilder.getMetadata(type);
        }
    }
    
    @Override
    public Map<Property<?>, MessageMetadata> getMetadata() {
        if (Objects.isNull(compoundMessageBuilder)) {
            return compoundMessage.getMetadata();
        } else {
            return Collections.unmodifiableMap(compoundMessageBuilder.getMetadata());
        }
    }
    
    @Override
    public <T extends MessageMetadata> T getMetadataOrFail(Property<T> type) {
        if (Objects.isNull(compoundMessageBuilder)) {
            return compoundMessage.getMetadataOrFail(type);
        } else {
            return compoundMessageBuilder.getMetadataOrFail(type);
        }
    }
    
    @Override
    public boolean containsMetadata(Property<?> type) {
        if (Objects.isNull(compoundMessageBuilder)) {
            return compoundMessage.containsMetadata(type);
        } else {
            return compoundMessageBuilder.containsMetadata(type);
        }
    }
    
    @Override
    public BasicMessage get(int index) {
        if (Objects.nonNull(compoundMessageBuilder)) {
            return compoundMessageBuilder.get(index);
        } else {
            return compoundMessage.get(index);
        }
    }
    
    @Override
    public CompoundMessage build() {
        if (Objects.nonNull(compoundMessageBuilder)) {
            compoundMessage = compoundMessageBuilder.build();
            compoundMessageBuilder = null;
        }
        return compoundMessage;
    }
    
    @Override
    public boolean isEmpty() {
        if (!compoundMessage.isEmpty()) {
            return false;
        }
        return !Objects.nonNull(compoundMessageBuilder) || compoundMessage.isEmpty();
    }
    
    @Override
    public Iterator<BasicMessage> iterator() {
        if (Objects.isNull(compoundMessageBuilder)) {
            return compoundMessage.iterator();
        } else {
            return compoundMessageBuilder.iterator();
        }
    }
}
