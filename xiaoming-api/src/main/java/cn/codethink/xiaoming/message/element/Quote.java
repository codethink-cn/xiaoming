package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.element.AbstractMessageElement;
import cn.codethink.common.util.Preconditions;

/**
 * 引用某条消息
 *
 * @author Chuanwise
 */
public class Quote
        extends AbstractMessageElement {
    
    protected final Message message;
    
    public Quote(Message message) {
        Preconditions.namedArgumentNonNull(message, "message");
        
        this.message = message;
    }
    
    @Override
    public String toMessageCode() {
        return "[quote:code=" + message.getCode() + "]";
    }
    
    @Override
    public String toContent() {
        return "[回复]";
    }
}
