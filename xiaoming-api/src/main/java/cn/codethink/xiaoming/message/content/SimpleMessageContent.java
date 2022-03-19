package cn.codethink.xiaoming.message.content;

import cn.codethink.common.collection.ListAdapter;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.content.MessageContent;
import cn.codethink.xiaoming.message.content.SimpleMessageContentBuilder;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.common.util.Joiner;

import java.util.List;

/**
 * 简单消息内容构造器
 *
 * @author Chuanwise
 */
public class SimpleMessageContent
        extends ListAdapter<MessageElement>
        implements MessageContent {
    
    SimpleMessageContent(List<MessageElement> messageElements) {
        super(messageElements);
    }
    
    public static SimpleMessageContentBuilder builder() {
        return new SimpleMessageContentBuilder();
    }
    
    @Override
    public String toMessageCode() {
        return Joiner.builder()
            .delimiter("")
            .build()
            .withAll(collection, Serializable::toMessageCode)
            .join();
    }
    
    @Override
    public String toContent() {
        return Joiner.builder()
            .delimiter("")
            .build()
            .withAll(collection, Serializable::toMessageCode)
            .join();
    }
    
    @Override
    public String toString() {
        return toMessageCode();
    }
    
}