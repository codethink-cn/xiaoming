package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.EmptyIterator;
import cn.chuanwise.common.util.SingletonIterator;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.element.BasicMessage;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.message.element.MessageMetadataType;
import cn.codethink.xiaoming.message.element.MetadataMessage;
import lombok.Data;

import java.util.*;

/**
 * 回应消息内容
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class SingletonCompoundMessage
    extends AbstractCompoundMessage
    implements CompoundMessage {
    
    protected final Message message;
    
    public SingletonCompoundMessage(Message message) {
        Preconditions.nonNull(message, "message");
        
        this.message = message;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public CompoundMessage plus(Message message) {
        cn.chuanwise.common.util.Preconditions.objectNonNull(message, "message");
    
        return new SimpleCompoundMessageBuilder(2)
            .plus(this.message)
            .plus(message)
            .build();
    }
    
    @Override
    public CompoundMessage plus(String text) {
        cn.chuanwise.common.util.Preconditions.objectArgumentNonEmpty(text, "text");
    
        return new SimpleCompoundMessageBuilder(2)
            .plus(this.message)
            .plus(text)
            .build();
    }
    
    @Override
    public CompoundMessage plus(Message... messages) {
        cn.chuanwise.common.util.Preconditions.objectNonNull(messages, "messages");
        
        if (Arrays.isEmpty(messages)) {
            return this;
        }
    
        return new SimpleCompoundMessageBuilder()
            .plus(this.message)
            .plus(messages)
            .build();
    }
    
    @Override
    public CompoundMessage plus(Iterable<? extends Message> iterable) {
        cn.chuanwise.common.util.Preconditions.objectNonNull(iterable, "iterable");
    
        return new SimpleCompoundMessageBuilder()
            .plus(this.message)
            .plus(iterable)
            .build();
    }
    
    @Override
    public Iterator<BasicMessage> iterator() {
        if (message instanceof BasicMessage) {
            final BasicMessage basicMessage = (BasicMessage) message;
            return new SingletonIterator<>(basicMessage);
        } else {
            return EmptyIterator.getInstance();
        }
    }
    
    @Override
    public int size() {
        return message instanceof MetadataMessage
            ? 0
            : 1;
    }
    
    @Override
    public <T> T getMetadata(MessageMetadataType<T> type) {
        Preconditions.objectNonNull(type, "type");
    
        if (message instanceof MetadataMessage) {
            final MetadataMessage metadataMessage = (MetadataMessage) message;
            if (metadataMessage.getMetadataType() == type) {
                return (T) metadataMessage;
            }
        }
        
        return null;
    }
    
    @Override
    public Map<MessageMetadataType<?>, Object> getMetadata() {
        if (message instanceof MetadataMessage) {
            final MetadataMessage metadataMessage = (MetadataMessage) message;
            return Collections.singletonMap(metadataMessage.getMetadataType(), metadataMessage);
        } else {
            return Collections.emptyMap();
        }
    }
}
