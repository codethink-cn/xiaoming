package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.EmptyIterator;
import cn.chuanwise.common.util.SingletonIterator;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.message.basic.MessageMetadataType;
import cn.codethink.xiaoming.message.basic.MessageMetadata;

import java.util.*;

/**
 * 回应消息内容
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public class SingletonCompoundMessage
    extends AbstractCompoundMessage
    implements CompoundMessage {
    
    protected final BasicMessage basicMessage;
    
    public SingletonCompoundMessage(BasicMessage basicMessage) {
        Preconditions.nonNull(basicMessage, "message");
        
        this.basicMessage = basicMessage;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public CompoundMessage plus(Message message) {
        Preconditions.objectNonNull(message, "message");
    
        return new SimpleCompoundMessageBuilder(2)
            .plus(this.basicMessage)
            .plus(message)
            .build();
    }
    
    @Override
    public CompoundMessage plus(String text) {
        Preconditions.objectArgumentNonEmpty(text, "text");
    
        return new SimpleCompoundMessageBuilder(2)
            .plus(this.basicMessage)
            .plus(text)
            .build();
    }
    
    @Override
    public CompoundMessage plus(MessageMetadata messageMetadata) {
        Preconditions.objectNonNull(basicMessage, "message");
    
        return new SimpleCompoundMessageBuilder(2)
            .plus(this.basicMessage)
            .plus(messageMetadata)
            .build();
    }
    
    @Override
    public CompoundMessage plus(Message... messages) {
        Preconditions.objectNonNull(messages, "messages");
        
        if (Arrays.isEmpty(messages)) {
            return this;
        }
    
        return new SimpleCompoundMessageBuilder()
            .plus(this.basicMessage)
            .plus(messages)
            .build();
    }
    
    @Override
    public CompoundMessage plus(Iterable<? extends Message> iterable) {
        Preconditions.objectNonNull(iterable, "iterable");
    
        return new SimpleCompoundMessageBuilder()
            .plus(this.basicMessage)
            .plus(iterable)
            .build();
    }
    
    @Override
    public Iterator<BasicMessage> iterator() {
        return EmptyIterator.getInstance();
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public <T extends MessageMetadata> T getMetadata(MessageMetadataType<T> type) {
        return null;
    }
    
    @Override
    public Map<MessageMetadataType<? extends MessageMetadata>, MessageMetadata> getMetadata() {
        return Collections.emptyMap();
    }
    
    @Override
    public BasicMessage get(int index) {
        Preconditions.objectIndex(index, 1, "basic message");
        return basicMessage;
    }
}
