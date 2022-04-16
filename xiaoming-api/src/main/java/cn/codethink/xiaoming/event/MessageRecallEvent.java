package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.contact.Sender;
import lombok.Data;

/**
 * 消息撤回事件
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class MessageRecallEvent
    extends AbstractEvent
    implements OnlineMessageEvent {
    
    /**
     * 消息本体
     */
    private final Message message;
    
    /**
     * 消息发送方
     */
    private final Sender sender;
    
    /**
     * 消息目标
     */
    private final Contact target;
    
    /**
     * 时间戳
     */
    private final long timestamp;
    
    public MessageRecallEvent(Sender sender, Message message, Contact target, long timestamp) {
        Preconditions.nonNull(sender, "sender");
        Preconditions.nonNull(target, "target");
        Preconditions.nonNull(message, "message");
        
        this.message = message;
        this.sender = sender;
        this.target = target;
        this.timestamp = timestamp;
    }
    
    @Override
    public Bot getBot() {
        return sender.getBot();
    }
}
