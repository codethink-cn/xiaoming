package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送给群成员的消息源
 *
 * @author Chuanwise
 */
public class MiraiToGroupMemberMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements ToGroupMemberMessageSource {
    
    public MiraiToGroupMemberMessageSource(OnlineMessageSource.Outgoing.ToTemp miraiMessageSource,
                                           MiraiMember target,
                                           Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, target.getBot(), target, properties);
    }
    
    @Override
    public Bot getSender() {
        return (Bot) super.getSender();
    }
    
    @Override
    public GroupMember getTarget() {
        return (GroupMember) super.getTarget();
    }
    
    @Override
    public Code getMassCode() {
        return getMass().getCode();
    }
    
    @Override
    public Mass getMass() {
        return getTarget().getMass();
    }
}
