package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Group;
import cn.codethink.xiaoming.contact.GroupMember;
import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;

/**
 * 来自群消息的消息引用
 *
 * @author Chuanwise
 */
public class MiraiFromGroupMessageReference
    extends AbstractMiraiOnlineMessageReference
    implements FromGroupMessageReference {
    
    public MiraiFromGroupMessageReference(OnlineMessageSource.Incoming.FromGroup miraiMessageSource, GroupMember sender) {
        super(miraiMessageSource, sender, sender.getMass());
    }
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("source")
            .argument("mirai")
            .argument("online")
            .argument("incoming")
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
    public GroupMember getSender() {
        return (GroupMember) super.getSender();
    }
    
    @Override
    public Group getTarget() {
        return (Group) super.getTarget();
    }
    
    @Override
    public Code getMassCode() {
        return getTarget().getCode();
    }
    
    @Override
    public Group getMass() {
        return getTarget();
    }
}
