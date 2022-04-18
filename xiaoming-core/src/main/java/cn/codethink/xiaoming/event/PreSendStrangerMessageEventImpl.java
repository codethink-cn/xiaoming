package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.event.PreSendStrangerMessageEvent
 */
@Data
public class PreSendStrangerMessageEventImpl
    extends AbstractCancellableEvent
    implements PreSendStrangerMessageEvent {
    
    private final Stranger stranger;
    
    private Message message;
    
    public PreSendStrangerMessageEventImpl(Stranger stranger, Message message) {
        Preconditions.objectNonNull(stranger, "stranger");
        Preconditions.objectNonNull(message, "message");
        
        this.stranger = stranger;
        this.message = message;
    }
    
    @Override
    public void setMessage(Message message) {
        Preconditions.objectNonNull(message, "message");
        
        this.message = message;
    }
    
    @Override
    public Bot getBot() {
        return stranger.getBot();
    }
    
    @Override
    public Stranger getTarget() {
        return stranger;
    }
}
