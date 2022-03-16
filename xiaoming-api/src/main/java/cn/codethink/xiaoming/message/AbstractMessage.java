package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.content.MessageContent;
import lombok.Data;

/**
 * @see cn.codethink.xiaoming.message.Message
 * @author Chuanwise
 */
@Data
public class AbstractMessage
        implements Message {
    
    protected final Code code;
    
    protected final MessageContent messageContent;
    
    protected final long timeMillis;
}
