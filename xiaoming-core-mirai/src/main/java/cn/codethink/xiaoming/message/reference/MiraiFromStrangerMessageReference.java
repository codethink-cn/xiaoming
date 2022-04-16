package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.contact.MiraiStranger;
import cn.codethink.xiaoming.message.MessageCode;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * @author Chuanwise
 * @see FromStrangerMessageReference
 */
public class MiraiFromStrangerMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements FromStrangerMessageReference {
    
    public MiraiFromStrangerMessageReference(OnlineMessageSource.Incoming.FromStranger miraiMessageSource, MiraiStranger sender) {
        super(miraiMessageSource, sender, sender.getBot());
    }
    
    @Override
    public MiraiStranger getSender() {
        return (MiraiStranger) super.getSender();
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
