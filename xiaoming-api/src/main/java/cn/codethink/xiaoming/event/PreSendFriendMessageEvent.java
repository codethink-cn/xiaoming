package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 即将发送好友消息事件
 *
 * @author Chuanwise
 */
@Data
public class PreSendFriendMessageEvent
    extends AbstractCancellableEvent
    implements PreSendMessageEvent, FriendEvent {
    
    private final Friend friend;
    
    private Message message;
    
    public PreSendFriendMessageEvent(Friend friend, Message message) {
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
