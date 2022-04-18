package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import lombok.Data;

/**
 * 群消息撤回事件
 *
 * @author Chuanwise
 */
@Data
public class FriendMessageRecallEvent
    extends AbstractEvent
    implements MessageRecallEvent, FriendEvent {
    
    private final CompoundMessage message;
    
    private final long timestamp;
    
    private final Friend friend;
    
    public FriendMessageRecallEvent(Friend friend, CompoundMessage message, long timestamp) {
        
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
