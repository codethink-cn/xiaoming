package cn.codethink.xiaoming.message.content;

import cn.codethink.xiaoming.message.content.MessageContent;
import cn.codethink.xiaoming.message.content.MessageContentBuildable;
import cn.codethink.xiaoming.message.content.SimpleMessageContent;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.common.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 消息内容构造器
 *
 * @author Chuanwise
 */
public class SimpleMessageContentBuilder
    implements MessageContentBuildable {
    
    protected final List<MessageElement> messageElements = new ArrayList<>();
    
    @Override
    public SimpleMessageContentBuilder asMessageContentBuilder() {
        return this;
    }
    
    @Override
    public SimpleMessageContentBuilder plus(MessageElement messageElement) {
        Preconditions.namedArgumentNonNull(messageElement, "message element");
        
        messageElements.add(messageElement);
        
        return this;
    }
    
    @Override
    public SimpleMessageContentBuilder plusAll(Iterable<MessageElement> messageElements) {
        Preconditions.namedArgumentNonNull(messageElements, "message elements");
        
        messageElements.forEach(this.messageElements::add);
        
        return this;
    }
    
    public MessageContent build() {
        return new SimpleMessageContent(
            Collections.unmodifiableList(messageElements)
        );
    }
}
