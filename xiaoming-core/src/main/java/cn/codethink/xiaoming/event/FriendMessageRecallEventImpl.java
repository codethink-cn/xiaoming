package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see FriendMessageRecallEvent
 */
@Data
public class FriendMessageRecallEventImpl
    extends AbstractEvent
    implements FriendMessageRecallEvent {
    
    private final CompoundMessage message;
    
    private final long timestamp;
    
    private final Friend friend;
    
    public FriendMessageRecallEventImpl(Friend friend, CompoundMessage message, long timestamp) {
        
        Preconditions.objectNonNull(friend, "friend");
        Preconditions.objectNonNull(timestamp, "timestamp");
        
        this.friend = friend;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    @Override
    public Bot getBot() {
        return friend.getBot();
    }
    
    @Override
    public Friend getSender() {
        return friend;
    }
    
    @Override
    public Friend getTarget() {
        return friend;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
