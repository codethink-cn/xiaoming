package cn.codethink.xiaoming.message.reference;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.MessageCode;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * 来自群临时会话的消息引用
 *
 * @author Chuanwise
 */
public class MiraiFromGroupMemberMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements FromGroupMemberMessageReference {
    
    public MiraiFromGroupMemberMessageReference(OnlineMessageSource.Incoming.FromTemp miraiMessageSource, MiraiMember sender) {
        super(miraiMessageSource, sender, sender.getBot());
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
    public String serializeToMessageCode() {
        return MessageCode.builder("source")
            .argument("mirai")
            .argument("online")
            .argument("incoming")
            .argument("member")
            .argument(miraiMessageSource.getBotId())
            .argument(miraiMessageSource.getTime())
            .argument(Arrays.toString(miraiMessageSource.getIds()))
            .argument(Arrays.toString(miraiMessageSource.getInternalIds()))
            .argument(miraiMessageSource.getFromId())
            .argument(miraiMessageSource.getTargetId())
            .argument(miraiMessageSource.getOriginalMessage().serializeToMiraiCode())
            .build();
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
