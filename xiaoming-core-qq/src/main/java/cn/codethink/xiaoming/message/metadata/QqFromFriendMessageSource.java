package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.contact.QqFriend;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * @author Chuanwise
 * @see FromFriendMessageSource
 */
public class QqFromFriendMessageSource
    extends AbstractQqOnlineMessageSource
    implements FromFriendMessageSource {
    
    public QqFromFriendMessageSource(OnlineMessageSource.Incoming.FromFriend qqMessageSource,
                                     QqFriend sender,
                                     Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, sender, sender.getBot(), properties);
    }
    
    @Override
    public QqFriend getSender() {
        return (QqFriend) super.getSender();
    }
    
    @Override
    public QqBot getTarget() {
        return (QqBot) super.getTarget();
    }
}
