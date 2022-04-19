package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 来自群临时会话的消息源
 *
 * @author Chuanwise
 */
public class QqFromGroupMemberMessageSource
    extends AbstractQqOnlineMessageSource
    implements FromGroupMemberMessageSource {
    
    public QqFromGroupMemberMessageSource(OnlineMessageSource.Incoming.FromTemp qqMessageSource,
                                          QqMember sender,
                                          Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, sender, sender.getBot(), properties);
    }
    
    @Override
    public QqMember getSender() {
        return (QqMember) super.getSender();
    }
    
    @Override
    public QqBot getBot() {
        return (QqBot) super.getBot();
    }
    
    @Override
    public QqBot getTarget() {
        return (QqBot) super.getTarget();
    }
    
    @Override
    public Code getMassCode() {
        return getMass().getCode();
    }
    
    @Override
    public QqGroup getMass() {
        return getSender().getMass();
    }
}
