package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.recept.Receptionist;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;

@Getter
public class MemberXiaomingUserImpl extends XiaomingUserImpl<MemberContact> implements MemberXiaomingUser {
    final MemberContact contact;

    public MemberXiaomingUserImpl(MemberContact memberContact) {
        super(memberContact.getXiaomingBot(), memberContact.getCode());
        this.contact = memberContact;
    }

    @Override
    public long getCode() {
        return contact.getCode();
    }

    @Override
    public String getName() {
        return contact.getName();
    }

    @Override
    public String getCompleteName() {
        return contact.getAliasAndCode();
    }
}
