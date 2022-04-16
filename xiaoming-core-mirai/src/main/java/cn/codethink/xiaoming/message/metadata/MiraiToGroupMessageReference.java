package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.MiraiGroup;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * 发送到群内的消息引用
 *
 * @author Chuanwise
 */
public class MiraiToGroupMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements ToGroupMessageReference {
    
    public MiraiToGroupMessageReference(OnlineMessageSource.Outgoing.ToGroup miraiMessageSource, MiraiGroup miraiGroup) {
        super(miraiMessageSource, miraiGroup.getBot(), miraiGroup);
    }
    
    @Override
    public MiraiGroup getTarget() {
        return (MiraiGroup) super.getTarget();
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
            .argument("group")
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
        return getTarget().getCode();
    }
    
    @Override
    public MiraiGroup getMass() {
        return getTarget();
    }
}
