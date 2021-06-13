package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;

public interface MemberMessage extends Message {
    @Override
    MemberXiaomingUser getSender();

    @Override
    MemberContact getContact();

    @Override
    MemberMessage clone() throws CloneNotSupportedException;
}
