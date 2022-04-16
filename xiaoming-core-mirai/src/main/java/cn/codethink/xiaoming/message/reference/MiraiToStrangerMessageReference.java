package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiStranger;
import cn.codethink.xiaoming.contact.Stranger;
import cn.codethink.xiaoming.message.MessageCode;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * 发送给陌生人的消息引用
 *
 * @author Chuanwise
 */
public class MiraiToStrangerMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements ToStrangerMessageReference {
    
    public MiraiToStrangerMessageReference(OnlineMessageSource miraiMessageSource, Stranger stranger) {
        super(miraiMessageSource, stranger.getBot(), stranger);
    }
    
    @Override
    public MiraiBot getSender() {
        return (MiraiBot) super.getSender();
    }
    
    @Override
    public MiraiStranger getTarget() {
        return (MiraiStranger) super.getTarget();
    }
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("source")
            .argument("mirai")
            .argument("online")
            .argument("outgoing")
            .argument("stranger")
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
