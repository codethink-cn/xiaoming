package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiFriend;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * @author Chuanwise
 * @see FromFriendMessageSource
 */
public class MiraiFromFriendMessageSource
    extends AbstractMiraiOnlineMessageSource
    implements FromFriendMessageSource {
    
    public MiraiFromFriendMessageSource(OnlineMessageSource.Incoming.FromFriend miraiMessageSource,
                                        MiraiFriend sender,
                                        Map<Property<?>, Object> properties) {
        
        super(miraiMessageSource, sender, sender.getBot(), properties);
    }
    
    @Override
    public MiraiFriend getSender() {
        return (MiraiFriend) super.getSender();
    }
    
    @Override
    public MiraiBot getTarget() {
        return (MiraiBot) super.getTarget();
    }
}
