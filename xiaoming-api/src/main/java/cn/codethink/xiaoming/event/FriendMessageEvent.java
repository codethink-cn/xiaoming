package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 被动地收到好友私聊消息
 *
 * @author Chuanwise
 */
@Data
public class FriendMessageEvent
        implements MessageEvent {
    
    protected final Friend sender;
    
    protected final Message message;
    
    protected final long timeMillis;
    
    public FriendMessageEvent(Friend sender, Message message) {
        Preconditions.namedArgumentNonNull(sender, "sender");
        Preconditions.namedArgumentNonNull(message, "message");
        
        this.sender = sender;
        this.message = message;
        this.timeMillis = message.getTimeMillis();
    }
    
    @Override
    public Bot getBot() {
        return sender.getBot();
    }
    
    @Override
    public Friend getSender() {
        return sender;
    }
}
