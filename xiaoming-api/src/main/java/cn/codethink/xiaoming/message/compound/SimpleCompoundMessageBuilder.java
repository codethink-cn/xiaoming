package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.element.BasicMessage;
import cn.codethink.xiaoming.message.element.MessageMetadataType;
import cn.codethink.xiaoming.message.element.MetadataMessage;
import cn.codethink.xiaoming.message.element.Text;

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
    
    /**
     * 基础消息
     */
    private final List<BasicMessage> basicMessages;
    
    /**
     * 消息元数据
     */
    private final Map<MessageMetadataType<?>, Object> metadata;
    
    SimpleCompoundMessageBuilder() {
        this.basicMessages = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    SimpleCompoundMessageBuilder(CompoundMessage compoundMessage) {
        Preconditions.objectNonNull(compoundMessage, "compound message");
        
        // copy basic messages
        this.basicMessages = new ArrayList<>(compoundMessage.size());
        compoundMessage.forEach(basicMessages::add);
        
        // copy metadata
        this.metadata = new HashMap<>(compoundMessage.getMetadata());
    }
    
    SimpleCompoundMessageBuilder(int capacity) {
        Preconditions.argument(capacity >= 0, "capacity must be bigger than or equals to 0!");
        
        this.basicMessages = new ArrayList<>(capacity);
        this.metadata = new HashMap<>();
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
        
        basicMessages.add(new Text(text));
        
        return this;
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(Message message) {
        Preconditions.objectNonNull(message, "message");
    
        if (message instanceof MetadataMessage) {
            final MetadataMessage metadataMessage = (MetadataMessage) message;
            metadata.put(metadataMessage.getMetadataType(), metadataMessage);
            return this;
        }
        if (message instanceof BasicMessage) {
            final BasicMessage basicMessage = (BasicMessage) message;
        
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
            if (message instanceof MetadataMessage) {
                final MetadataMessage metadataMessage = (MetadataMessage) message;
                metadata.put(metadataMessage.getMetadataType(), metadataMessage);
                continue;
            }
            if (message instanceof BasicMessage) {
                final BasicMessage basicMessage = (BasicMessage) message;
                basicMessages.add(basicMessage);
            }
            if (message instanceof CompoundMessage) {
                final CompoundMessage compoundMessage = (CompoundMessage) message;
                compoundMessage.forEach(basicMessages::add);
            }
        
            throw new UnsupportedOperationException("message is not basic message or compound message!");
        }
    
        return this;
    }
    
    @Override
    public SimpleCompoundMessageBuilder plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        for (Message message : iterable) {
            if (message instanceof MetadataMessage) {
                final MetadataMessage metadataMessage = (MetadataMessage) message;
                metadata.put(metadataMessage.getMetadataType(), metadataMessage);
                continue;
            }
            if (message instanceof BasicMessage) {
                final BasicMessage basicMessage = (BasicMessage) message;
                basicMessages.add(basicMessage);
            }
            if (message instanceof CompoundMessage) {
                final CompoundMessage compoundMessage = (CompoundMessage) message;
                compoundMessage.forEach(basicMessages::add);
            }
        
            throw new UnsupportedOperationException("message is not basic message or compound message!");
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
    public <T> T getMetadata(MessageMetadataType<T> type) {
        Preconditions.objectNonNull(type, "type");
        return (T) metadata.get(type);
    }
    
    @Override
    public Map<MessageMetadataType<?>, Object> getMetadata() {
        return metadata;
    }
}
