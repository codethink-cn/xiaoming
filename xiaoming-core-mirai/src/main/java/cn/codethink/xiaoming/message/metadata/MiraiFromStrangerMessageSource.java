package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiStranger;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * @author Chuanwise
 * @see FromStrangerMessageSource
 */
public class MiraiFromStrangerMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements FromStrangerMessageSource {
    
    public MiraiFromStrangerMessageSource(OnlineMessageSource.Incoming.FromStranger miraiMessageSource,
                                          MiraiStranger sender,
                                          Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, sender, sender.getBot(), properties);
    }
    
    @Override
    public MiraiStranger getSender() {
        return (MiraiStranger) super.getSender();
    }
    
    @Override
    public MiraiBot getTarget() {
        return (MiraiBot) super.getTarget();
    }
}
