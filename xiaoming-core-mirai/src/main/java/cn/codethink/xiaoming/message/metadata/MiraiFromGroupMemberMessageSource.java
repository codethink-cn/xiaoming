package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
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
public class MiraiFromGroupMemberMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements FromGroupMemberMessageSource {
    
    public MiraiFromGroupMemberMessageSource(OnlineMessageSource.Incoming.FromTemp miraiMessageSource,
                                             MiraiMember sender,
                                             Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, sender, sender.getBot(), properties);
    }
    
    @Override
    public MiraiMember getSender() {
        return (MiraiMember) super.getSender();
    }
    
    @Override
    public MiraiBot getBot() {
        return (MiraiBot) super.getBot();
    }
    
    @Override
    public MiraiBot getTarget() {
        return (MiraiBot) super.getTarget();
    }
    
    @Override
    public Code getMassCode() {
        return getMass().getCode();
    }
    
    @Override
    public MiraiGroup getMass() {
        return getSender().getMass();
    }
}
