package cn.chuanwise.xiaoming.api.contact.message;

import cn.chuanwise.xiaoming.api.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.api.user.MemberXiaomingUser;

public interface MemberMessage extends Message {
    @Override
    MemberXiaomingUser getSender();

    @Override
    MemberContact getContact();

    @Override
    MemberMessage clone() throws CloneNotSupportedException;
}
