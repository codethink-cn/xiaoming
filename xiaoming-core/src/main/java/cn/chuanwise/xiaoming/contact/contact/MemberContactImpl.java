package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.contact.message.MemberMessageImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 表示群中的成员。不一定是小明用户
 * @author Chuanwise
 */
@Getter
public class MemberContactImpl extends XiaomingContactImpl<MemberMessage, NormalMember> implements MemberContact {
    final NormalMember miraiContact;
    final GroupContact groupContact;
    final List<MemberMessage> recentMessages;

    public MemberContactImpl(GroupContact groupContact, NormalMember miraiContact) {
        super(groupContact.getXiaomingBot());
        this.miraiContact = miraiContact;
        this.groupContact = groupContact;
        this.recentMessages = new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize());
    }

    @Override
    public NormalMember getMiraiContact() {
        return miraiContact;
    }

    @Override
    public void mute(long timeMillis) {
        miraiContact.mute((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis));
    }

    @Override
    public MemberMessage send(MessageChain messages) {
        return getXiaomingBot().getResourceManager().useResources(new MemberMessageImpl(getXiaomingBot().getReceptionistManager().getBotReceptionist().forMember(getGroupCode()),
                miraiContact.sendMessage(messages).getSource().getOriginalMessage()));
    }
}