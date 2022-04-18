package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * @author Chuanwise
 */
@Data
public class PreSendFriendMessageEventImpl
    extends AbstractCancellableEvent
    implements PreSendFriendMessageEvent {
    
    private final Friend friend;
    
    private Message message;
    
    public PreSendFriendMessageEventImpl(Friend friend, Message message) {
        Preconditions.objectNonNull(friend, "friend");
        Preconditions.objectNonNull(message, "message");
        
        this.friend = friend;
        this.message = message;
    }
    
    @Override
    public void setMessage(Message message) {
        Preconditions.objectNonNull(message, "message");
        
        this.message = message;
    }
    
    @Override
    public Bot getBot() {
        return friend.getBot();
    }
    
    @Override
    public Friend getTarget() {
        return friend;
    }
}
