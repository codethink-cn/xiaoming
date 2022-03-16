package cn.codethink.xiaoming.message.content;

import cn.codethink.collection.ListAdapter;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.util.Joiner;
import cn.codethink.util.Preconditions;
import lombok.Data;

import java.util.List;

/**
 * 简单消息内容构造器
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class ReferenceMessageContent
        extends ListAdapter<MessageElement>
        implements MessageContent {
    
    private final Message referredMessage;
    
    ReferenceMessageContent(Message referredMessage, List<MessageElement> messageElements) {
        super(messageElements);
    
        Preconditions.namedArgumentNonNull(referredMessage, "referred message");
        
        this.referredMessage = referredMessage;
    }
    
    public static ReferenceMessageContentBuilder builder() {
        return new ReferenceMessageContentBuilder();
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
    
    /**
     * 获取消息元素
     *
     * @return 消息元素
     */
    public List<MessageElement> getMessageElements() {
        return getList();
    }
    
    @Override
    public String toString() {
        return toMessageCode();
    }
    
}