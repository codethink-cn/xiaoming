package cn.codethink.xiaoming.message.content;

import cn.codethink.xiaoming.message.content.SimpleMessageContent;
import cn.codethink.xiaoming.message.element.MessageElement;
import cn.codethink.common.util.Preconditions;
import lombok.Data;

import java.util.Collections;

/**
 * 回应消息内容
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class SingletonMessageContent
        extends SimpleMessageContent {
    
    protected final MessageElement singletonElement;
    
    public SingletonMessageContent(MessageElement singletonElement) {
        super(Collections.singletonList(singletonElement));
    
        Preconditions.namedArgumentNonNull(singletonElement, "flashImage");
        
        this.singletonElement = singletonElement;
    }
    
    public MessageElement getSingletonElement() {
        return singletonElement;
    }
}
