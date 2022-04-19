package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.contact.QqStranger;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送给陌生人的消息源
 *
 * @author Chuanwise
 */
public class QqToStrangerMessageSource
    extends AbstractQqOnlineMessageSource
    implements ToStrangerMessageSource {
    
    public QqToStrangerMessageSource(OnlineMessageSource qqMessageSource,
                                     Stranger stranger,
                                     Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, stranger.getBot(), stranger, properties);
    }
    
    @Override
    public QqBot getSender() {
        return (QqBot) super.getSender();
    }
    
    @Override
    public QqStranger getTarget() {
        return (QqStranger) super.getTarget();
    }
}
