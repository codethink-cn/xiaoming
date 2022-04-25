package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupSender;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 来自群消息的消息源
 *
 * @author Chuanwise
 */
public class QqFromGroupMessageSource
    extends AbstractQqOnlineMessageSource
    implements FromGroupMessageSource {
    
    public QqFromGroupMessageSource(OnlineMessageSource.Incoming.FromGroup qqMessageSource,
                                    GroupSender sender,
                                    Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, sender, sender.getMass(), properties);
    }
    
    @Override
    public GroupSender getSender() {
        return (GroupSender) super.getSender();
    }
    
    @Override
    public Group getTarget() {
        return (Group) super.getTarget();
    }
    
    @Override
    public Code getMassCode() {
        return getTarget().getCode();
    }
    
    @Override
    public Group getMass() {
        return getTarget();
    }
}
