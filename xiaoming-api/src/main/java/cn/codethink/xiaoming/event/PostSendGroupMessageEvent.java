package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.metadata.ToGroupMessageReference;
import lombok.Data;

/**
 * 已发送群消息事件
 *
 * @author Chuanwise
 */
@Data
public class PostSendGroupMessageEvent
    extends AbstractEvent
    implements PostSendMassMessageEvent {
    
    private final Group mass;
    
    private final Message message;
    
    private final ToGroupMessageReference messageReference;
    
    @Override
    public Bot getBot() {
        return mass.getBot();
    }
    
    @Override
    public Group getTarget() {
        return mass;
    }
    
    @Override
    public long getTimestamp() {
        return messageReference.getTimestamp();
    }
}
