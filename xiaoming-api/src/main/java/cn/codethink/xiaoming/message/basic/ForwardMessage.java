package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 合并转发中的单个消息内容。
 * 不一定是真正存在的消息。
 *
 * @author Chuanwise
 */
@Data
public class ForwardMessage {
    
    /**
     * 消息发送者编号
     */
    protected final Code senderCode;
    
    /**
     * 消息发送者名
     */
    protected final String senderName;
    
    /**
     * 消息时间戳
     */
    protected final long timestamp;
    
    /**
     * 消息内容
     */
    protected final Message message;
}
