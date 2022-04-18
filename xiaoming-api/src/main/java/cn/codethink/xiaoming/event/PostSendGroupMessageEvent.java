package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.ToGroupMessageSource;
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
    
    private final CompoundMessage message;
    
    private final ToGroupMessageSource messageSource;
    
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
        return messageSource.getTimestamp();
    }
}
