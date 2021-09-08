package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.recept.Receptionist;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@Getter
public class GroupXiaomingUserImpl extends XiaomingUserImpl<GroupContact> implements GroupXiaomingUser {
    final GroupContact contact;
    final MemberContact memberContact;

    public GroupXiaomingUserImpl(MemberContact contact) {
        super(contact.getXiaomingBot(), contact.getCode());
        this.contact = contact.getGroupContact();
        this.memberContact = contact;
    }

    @Override
    public long getCode() {
        return memberContact.getCode();
    }

    @Override
    public String getName() {
        return memberContact.getName();
    }

    @Override
    public String getCompleteName() {
        return "「" + contact.getAliasAndCode() + "」" + getName() + "（" + getCodeString() + "）";
    }
}
