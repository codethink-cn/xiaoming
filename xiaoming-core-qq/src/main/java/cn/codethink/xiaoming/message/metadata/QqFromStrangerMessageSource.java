package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.contact.QqStranger;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * @author Chuanwise
 * @see FromStrangerMessageSource
 */
public class QqFromStrangerMessageSource
    extends AbstractQqOnlineMessageSource
    implements FromStrangerMessageSource {
    
    public QqFromStrangerMessageSource(OnlineMessageSource.Incoming.FromStranger qqMessageSource,
                                       QqStranger sender,
                                       Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, sender, sender.getBot(), properties);
    }
    
    @Override
    public QqStranger getSender() {
        return (QqStranger) super.getSender();
    }
    
    @Override
    public QqBot getTarget() {
        return (QqBot) super.getTarget();
    }
}
