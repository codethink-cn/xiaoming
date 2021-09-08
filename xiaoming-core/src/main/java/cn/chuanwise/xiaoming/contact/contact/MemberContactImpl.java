package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import lombok.Getter;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 表示群中的成员。不一定是小明用户
 * @author Chuanwise
 */
@Getter
public class MemberContactImpl extends XiaomingContactImpl<NormalMember> implements MemberContact {
    final GroupContact groupContact;

    public MemberContactImpl(GroupContact groupContact, NormalMember miraiContact) {
        super(groupContact.getXiaomingBot(), miraiContact);
        this.groupContact = groupContact;
    }

    @Override
    public NormalMember getMiraiContact() {
        return miraiContact;
    }

    @Override
    public void mute(long timeMillis) {
        miraiContact.mute((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis));
    }
}