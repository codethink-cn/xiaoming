package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.message.reference.ToStrangerMessageReference;
import lombok.Data;

/**
 * 已发送好友消息事件
 *
 * @author Chuanwise
 */
@Data
public class PostSendStrangerMessageEvent
    extends AbstractEvent
    implements PostSendMessageEvent, StrangerEvent, MessageReceipt {
    
    private final Stranger stranger;
    
    private final Message message;
    
    private final ToStrangerMessageReference messageReference;
    
    public PostSendStrangerMessageEvent(Stranger stranger, Message message, ToStrangerMessageReference messageReference) {
        Preconditions.objectNonNull(stranger, "stranger");
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(message, "message reference");
        
        this.stranger = stranger;
        this.message = message;
        this.messageReference = messageReference;
    }
    
    @Override
    public Bot getBot() {
        return stranger.getBot();
    }
    
    @Override
    public Message getMessage() {
        return message;
    }
    
    @Override
    public long getTimestamp() {
        return messageReference.getTimestamp();
    }
    
    @Override
    public Contact getTarget() {
        return stranger;
    }
}
