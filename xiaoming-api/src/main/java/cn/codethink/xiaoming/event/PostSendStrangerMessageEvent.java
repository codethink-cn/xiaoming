package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.message.metadata.ToStrangerMessageSource;
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
    
    private final CompoundMessage message;
    
    private final ToStrangerMessageSource messageSource;
    
    public PostSendStrangerMessageEvent(Stranger stranger, CompoundMessage message, ToStrangerMessageSource messageSource) {
        Preconditions.objectNonNull(stranger, "stranger");
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(message, "message reference");
        
        this.stranger = stranger;
        this.message = message;
        this.messageSource = messageSource;
    }
    
    @Override
    public Bot getBot() {
        return stranger.getBot();
    }
    
    @Override
    public CompoundMessage getMessage() {
        return message;
    }
    
    @Override
    public long getTimestamp() {
        return messageSource.getTimestamp();
    }
    
    @Override
    public Contact getTarget() {
        return stranger;
    }
}
