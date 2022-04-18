package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.metadata.MessageMetadataType;
import cn.codethink.xiaoming.property.Property;

import java.util.*;

/**
 * 复合消息构建器
 *
 * @author Chuanwise
 */
@InternalAPI
public class SimpleCompoundMessageBuilder
    extends AbstractCompoundMessageBuilder
    implements CompoundMessageBuilder {
    
    private final List<BasicMessage> basicMessages;
    
    private final Map<Property<?>, MessageMetadata> metadata;
    
    /**
     * 是否是独占类型的消息。
     * 如果是，{@link #basicMessages} 的长度必须是 1 且值是 {@link SingletonMessage} 的子类。
     */
    private boolean singleton;
    
    public SimpleCompoundMessageBuilder() {
        this.basicMessages = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public SimpleCompoundMessageBuilder(CompoundMessage compoundMessage) {
        Preconditions.objectNonNull(compoundMessage, "compound message");
        
        // copy basic messages
        this.basicMessages = new ArrayList<>(compoundMessage.size());
        compoundMessage.forEach(basicMessages::add);
        
        // copy metadata
        this.metadata = new HashMap<>(compoundMessage.getMetadata());
    }
    
    public SimpleCompoundMessageBuilder(int capacity) {
        Preconditions.argument(capacity >= 0, "capacity must be bigger than or equals to 0!");
        
        this.basicMessages = new ArrayList<>(capacity);
        this.metadata = new HashMap<>();
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
        
        if (singleton) {
            return this;
        }
        plus(Text.of(text));
        
        return this;
    }
    
    @Override
    public CompoundMessageBuilder plus(MessageMetadata messageMetadata) {
        Preconditions.objectNonNull(messageMetadata, "message metadata");
        
        metadata.put(messageMetadata.getMetadataType(), messageMetadata);
        
        return this;
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(Message message) {
        Preconditions.objectNonNull(message, "message");
        
        if (message instanceof SingletonMessage) {
            final SingletonMessage singletonMessage = (SingletonMessage) message;
            
            basicMessages.clear();
            basicMessages.add(singletonMessage);
            singleton = true;
            return this;
        }
        
        if (singleton) {
            return this;
        }
    
        if (message instanceof BasicMessage) {
            final BasicMessage basicMessage = (BasicMessage) message;
    
            // if this is starts with space
            // can skip it
            if (basicMessage instanceof Text) {
                final Text text = (Text) basicMessage;
                if (text.getText().startsWith(" ")) {
                    basicMessages.add(basicMessage);
                    return this;
                }
            }
    
            if (!basicMessages.isEmpty()) {
                // check the last basic message
                final BasicMessage lastBasicMessage = basicMessages.get(basicMessages.size() - 1);
    
                // if the last basic message is an instance of Text,
                // and it ends with at least one space, just append,
                // or else append a space text (Text.SPACE)
                if (lastBasicMessage instanceof Text) {
                    final Text text = (Text) lastBasicMessage;
        
                    // append a text
                    if (text.getText().endsWith(" ")) {
                        basicMessages.add(basicMessage);
                        return this;
                    }
                }
                
                // if the last basic message is an instance of SpacedMessage,
                // append a Text.SPACE firstly
                if (lastBasicMessage instanceof SpacedMessage) {
                    
                    basicMessages.add(Text.SPACE);
                } else if (basicMessage instanceof SpacedMessage) {
                    basicMessages.add(Text.SPACE);
                }
            }
            
            basicMessages.add(basicMessage);
            return this;
        }
        
        if (message instanceof CompoundMessage) {
            final CompoundMessage compoundMessage = (CompoundMessage) message;
        
            compoundMessage.forEach(basicMessages::add);
            return this;
        }
    
        throw new UnsupportedOperationException("message is not basic message or compound message!");
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(Message... messages) {
        Preconditions.objectNonNull(messages, "messages");
    
        if (Arrays.isEmpty(messages)) {
            return this;
        }
    
        for (Message message : messages) {
            plus(message);
        }
    
        return this;
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        for (Message message : iterable) {
            plus(message);
        }
    
        return this;
    }
    
    @Override
    public CompoundMessage build() {
        return new ListCompoundMessage(
            Collections.unmodifiableList(basicMessages),
            Collections.unmodifiableMap(metadata)
        );
    }
    
    @Override
    public boolean isEmpty() {
        return basicMessages.isEmpty();
    }
    
    @Override
    public Iterator<BasicMessage> iterator() {
        return basicMessages.iterator();
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
    public Map<Property<?>, MessageMetadata> getMetadata() {
        return metadata;
    }
    
    @Override
    public BasicMessage get(int index) {
        Preconditions.objectIndex(index, size(), "basic message");
        return basicMessages.get(index);
    }
}
