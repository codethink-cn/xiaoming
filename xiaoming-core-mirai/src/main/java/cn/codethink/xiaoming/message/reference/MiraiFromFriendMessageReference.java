package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.contact.MiraiFriend;
import cn.codethink.xiaoming.contact.Sender;
import cn.codethink.xiaoming.message.MessageCode;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * @author Chuanwise
 * @see FromFriendMessageReference
 */
public class MiraiFromFriendMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements FromFriendMessageReference {
    
    public MiraiFromFriendMessageReference(OnlineMessageSource.Incoming.FromFriend miraiMessageSource, MiraiFriend sender) {
        super(miraiMessageSource, sender, sender.getBot());
    }
    
    @Override
    public MiraiFriend getSender() {
        return (MiraiFriend) super.getSender();
    }
    
    @Override
    public MiraiBot getTarget() {
        return (MiraiBot) super.getTarget();
    }
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("source")
            .argument("mirai")
            .argument("online")
            .argument("incoming")
            .argument("friend")
            .argument(miraiMessageSource.getBotId())
            .argument(miraiMessageSource.getTime())
            .argument(Arrays.toString(miraiMessageSource.getIds()))
            .argument(Arrays.toString(miraiMessageSource.getInternalIds()))
            .argument(miraiMessageSource.getFromId())
            .argument(miraiMessageSource.getTargetId())
            .argument(miraiMessageSource.getOriginalMessage().serializeToMiraiCode())
            .build();
    }
}
