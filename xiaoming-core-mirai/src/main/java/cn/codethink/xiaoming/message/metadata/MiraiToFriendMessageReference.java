package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiFriend;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * 发送到私聊的消息引用
 *
 * @author Chuanwise
 */
public class MiraiToFriendMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements ToFriendMessageReference {
    
    public MiraiToFriendMessageReference(OnlineMessageSource.Outgoing.ToFriend miraiMessageSource, MiraiFriend target) {
        super(miraiMessageSource, target.getBot(), target);
    }
    
    @Override
    public MiraiFriend getTarget() {
        return (MiraiFriend) super.getTarget();
    }
    
    @Override
    public MiraiBot getSender() {
        return (MiraiBot) super.getSender();
    }
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("source")
            .argument("mirai")
            .argument("online")
            .argument("outgoing")
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
