package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.QqBot;
import cn.codethink.xiaoming.contact.QqFriend;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;

/**
 * 发送到私聊的消息源
 *
 * @author Chuanwise
 */
public class QqToFriendMessageSource
    extends AbstractQqOnlineMessageSource
    implements ToFriendMessageSource {
    
    public QqToFriendMessageSource(OnlineMessageSource.Outgoing.ToFriend qqMessageSource,
                                   QqFriend target,
                                   Map<Property<?>, Object> properties) {
        
        super(qqMessageSource, target.getBot(), target, properties);
    }
    
    @Override
    public QqFriend getTarget() {
        return (QqFriend) super.getTarget();
    }
    
    @Override
    public QqBot getSender() {
        return (QqBot) super.getSender();
    }
}
