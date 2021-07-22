package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;

public interface MemberMessage extends Message {
    @Override
    MemberXiaomingUser getSender();

    @Override
    MemberContact getContact();

    @Override
    MemberMessage clone() throws CloneNotSupportedException;
}
