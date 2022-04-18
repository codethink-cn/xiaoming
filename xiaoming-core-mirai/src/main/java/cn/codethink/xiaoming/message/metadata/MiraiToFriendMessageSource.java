package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiFriend;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送到私聊的消息源
 *
 * @author Chuanwise
 */
public class MiraiToFriendMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements ToFriendMessageSource {
    
    public MiraiToFriendMessageSource(OnlineMessageSource.Outgoing.ToFriend miraiMessageSource,
                                      MiraiFriend target,
                                      Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, target.getBot(), target, properties);
    }
    
    @Override
    public MiraiFriend getTarget() {
        return (MiraiFriend) super.getTarget();
    }
    
    @Override
    public MiraiBot getSender() {
        return (MiraiBot) super.getSender();
    }
}
