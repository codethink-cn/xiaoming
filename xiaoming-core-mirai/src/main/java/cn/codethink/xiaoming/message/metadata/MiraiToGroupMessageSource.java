package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.MiraiGroup;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送到群内的消息源
 *
 * @author Chuanwise
 */
public class MiraiToGroupMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements ToGroupMessageSource {
    
    public MiraiToGroupMessageSource(OnlineMessageSource.Outgoing.ToGroup miraiMessageSource,
                                     MiraiGroup miraiGroup,
                                     Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, miraiGroup.getBot(), miraiGroup, properties);
    }
    
    @Override
    public MiraiGroup getTarget() {
        return (MiraiGroup) super.getTarget();
    }
    
    @Override
    public MiraiBot getSender() {
        return (MiraiBot) super.getSender();
    }
    
    @Override
    public Code getMassCode() {
        return getTarget().getCode();
    }
    
    @Override
    public MiraiGroup getMass() {
        return getTarget();
    }
}
