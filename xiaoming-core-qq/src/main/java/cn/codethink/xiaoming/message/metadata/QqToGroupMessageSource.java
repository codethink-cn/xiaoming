package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.QqGroup;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送到群内的消息源
 *
 * @author Chuanwise
 */
public class QqToGroupMessageSource
    extends AbstractQqOnlineMessageSource
    implements ToGroupMessageSource {
    
    public QqToGroupMessageSource(OnlineMessageSource.Outgoing.ToGroup qqMessageSource,
                                  QqGroup qqGroup,
                                  Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, qqGroup.getBot(), qqGroup, properties);
    }
    
    @Override
    public QqGroup getTarget() {
        return (QqGroup) super.getTarget();
    }
    
    @Override
    public QqBot getSender() {
        return (QqBot) super.getSender();
    }
    
    @Override
    public Code getMassCode() {
        return getTarget().getCode();
    }
    
    @Override
    public QqGroup getMass() {
        return getTarget();
    }
}
