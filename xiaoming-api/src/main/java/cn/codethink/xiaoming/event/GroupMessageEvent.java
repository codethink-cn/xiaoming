package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.Guild;
import cn.codethink.xiaoming.contact.Member;
import cn.codethink.xiaoming.message.Message;
import lombok.Data;

/**
 * 被动地收到好友私聊消息
 *
 * @author Chuanwise
 */
@Data
public class GroupMessageEvent
        implements MessageEvent, ScopeMessageEvent {
    
    protected final Member sender;
    
    protected final Message message;
    
    protected final long timeMillis;
    
    protected final Group scope;
    
    public GroupMessageEvent(Member sender, Message message) {
        Preconditions.namedArgumentNonNull(sender, "sender");
        Preconditions.namedArgumentNonNull(message, "message");
        
        this.sender = sender;
        this.message = message;
        this.timeMillis = message.getTimeMillis();
        scope = ((Group) sender.getScope());
    }
    
    @Override
    public Bot getBot() {
        return sender.getBot();
    }
    
    @Override
    public Member getSender() {
        return sender;
    }
    
    @Override
    public Group getScope() {
        return scope;
    }
}
