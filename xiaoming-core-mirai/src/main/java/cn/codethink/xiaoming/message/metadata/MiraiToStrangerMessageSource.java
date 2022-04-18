package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiStranger;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送给陌生人的消息源
 *
 * @author Chuanwise
 */
public class MiraiToStrangerMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements ToStrangerMessageSource {
    
    public MiraiToStrangerMessageSource(OnlineMessageSource miraiMessageSource,
                                        Stranger stranger,
                                        Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, stranger.getBot(), stranger, properties);
    }
    
    @Override
    public MiraiBot getSender() {
        return (MiraiBot) super.getSender();
    }
    
    @Override
    public MiraiStranger getTarget() {
        return (MiraiStranger) super.getTarget();
    }
}
