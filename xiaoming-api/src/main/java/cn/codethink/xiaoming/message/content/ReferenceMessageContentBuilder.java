package cn.codethink.xiaoming.message.content;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.element.MessageElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 消息内容构造器
 *
 * @author Chuanwise
 */
public class ReferenceMessageContentBuilder {
    
    protected Message referredMessage;
    
    protected final List<MessageElement> messageElements = new ArrayList<>();
    
    public ReferenceMessageContentBuilder asMessageContentBuilder() {
        return this;
    }
    
    public ReferenceMessageContentBuilder plus(MessageElement messageElement) {
        Preconditions.namedArgumentNonNull(messageElement, "message element");
        
        messageElements.add(messageElement);
        
        return this;
    }
    
    public ReferenceMessageContentBuilder plusAll(Iterable<MessageElement> messageElements) {
        Preconditions.namedArgumentNonNull(messageElements, "message elements");
        
        messageElements.forEach(this.messageElements::add);
        
        return this;
    }
    
    public ReferenceMessageContentBuilder referTo(Message referredMessage) {
        Preconditions.namedArgumentNonNull(referredMessage, "referred message");
    
        this.referredMessage = referredMessage;
        
        return this;
    }
    
    public ReferenceMessageContent build() {
        return new ReferenceMessageContent(
            referredMessage,
            Collections.unmodifiableList(messageElements)
        );
    }
}
