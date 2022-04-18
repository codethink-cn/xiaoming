package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.ForwardElement
 */
@Data
public class ForwardElementImpl
    implements ForwardElement {
    
    private final Code senderCode;
    
    private final String senderName;
    
    private final long timestamp;
    
    private final Message message;
    
    public ForwardElementImpl(Code senderCode,
                              String senderName,
                              long timestamp,
                              Message message) {
    
        Preconditions.objectNonNull(senderCode, "sender code");
        Preconditions.objectArgumentNonEmpty(senderName, "sender name");
        Preconditions.objectNonNull(message, "message");
        
        this.senderCode = senderCode;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.message = message;
    }
}
