package cn.codethink.xiaoming.message.metadata;

import cn.chuanwise.common.util.Arrays;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.*;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;

/**
 * 发送给群成员的消息引用
 *
 * @author Chuanwise
 */
public class MiraiToGroupMemberMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements ToGroupMemberMessageReference {
    
    public MiraiToGroupMemberMessageReference(OnlineMessageSource.Outgoing.ToTemp miraiMessageSource, MiraiMember target) {
        super(miraiMessageSource, target.getBot(), target);
    }
    
    @Override
    public Bot getSender() {
        return (Bot) super.getSender();
    }
    
    @Override
    public GroupMember getTarget() {
        return (GroupMember) super.getTarget();
    }
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("source")
            .argument("mirai")
            .argument("online")
            .argument("outgoing")
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
    public Mass getMass() {
        return getTarget().getMass();
    }
}
