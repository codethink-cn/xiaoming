package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
import cn.codethink.xiaoming.message.metadata.ToFriendMessageReference;
import lombok.Data;

/**
 * 已发送好友消息事件
 *
 * @author Chuanwise
 */
@Data
public class PostSendFriendMessageEvent
    extends AbstractEvent
    implements PostSendMessageEvent, FriendEvent, MessageReceipt {
    
    private final Friend friend;
    
    private final Message message;
    
    private final ToFriendMessageReference messageReference;
    
    public PostSendFriendMessageEvent(Friend friend, Message message, ToFriendMessageReference messageReference) {
        Preconditions.objectNonNull(friend, "friend");
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(message, "message reference");
        
        this.friend = friend;
        this.message = message;
        this.messageReference = messageReference;
    }
    
    @Override
    public Bot getBot() {
        return friend.getBot();
    }
    
    @Override
    public Friend getFriend() {
        return friend;
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
    public Friend getTarget() {
        return friend;
    }
}
