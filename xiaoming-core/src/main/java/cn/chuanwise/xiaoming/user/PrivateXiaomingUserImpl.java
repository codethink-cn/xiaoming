package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PrivateXiaomingUserImpl extends XiaomingUserImpl<PrivateContact> implements PrivateXiaomingUser {
    final PrivateContact contact;

    public PrivateXiaomingUserImpl(PrivateContact contact) {
        super(contact.getXiaomingBot(), contact.getCode());
        this.contact = contact;
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
