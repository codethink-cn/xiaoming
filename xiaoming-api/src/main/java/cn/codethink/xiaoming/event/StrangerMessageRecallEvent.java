package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import lombok.Data;

/**
 * 陌生人消息撤回事件
 *
 * @author Chuanwise
 */
@Data
public class StrangerMessageRecallEvent
    extends AbstractEvent
    implements MessageRecallEvent, StrangerEvent {
    
    private final CompoundMessage message;
    
    private final long timestamp;
    
    private final Stranger stranger;
    
    public StrangerMessageRecallEvent(Stranger stranger, CompoundMessage message, long timestamp) {
        
        Preconditions.objectNonNull(stranger, "friend");
        Preconditions.objectNonNull(timestamp, "timestamp");
        
        this.stranger = stranger;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    @Override
    public Bot getBot() {
        return stranger.getBot();
    }
    
    @Override
    public Stranger getSender() {
        return stranger;
    }
    
    @Override
    public Stranger getTarget() {
        return stranger;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
