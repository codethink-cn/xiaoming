package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.ToFriendMessageSource;
import cn.codethink.xiaoming.message.receipt.MessageReceipt;
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
    
    private final CompoundMessage message;
    
    private final ToFriendMessageSource messageSource;
    
    public PostSendFriendMessageEvent(Friend friend, CompoundMessage message, ToFriendMessageSource messageSource) {
        Preconditions.objectNonNull(friend, "friend");
        Preconditions.objectNonNull(message, "message");
        Preconditions.objectNonNull(message, "message reference");
        
        this.friend = friend;
        this.message = message;
        this.messageSource = messageSource;
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
    public CompoundMessage getMessage() {
        return message;
    }
    
    @Override
    public long getTimestamp() {
        return messageSource.getTimestamp();
    }
    
    @Override
    public Friend getTarget() {
        return friend;
    }
}
