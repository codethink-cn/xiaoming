package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 即将发送好友消息事件
 *
 * @author Chuanwise
 */
@Data
public class PreSendStrangerMessageEvent
    extends AbstractCancellableEvent
    implements PreSendMessageEvent, StrangerEvent {
    
    private final Stranger stranger;
    
    private Message message;
    
    public PreSendStrangerMessageEvent(Stranger stranger, Message message) {
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
