package cn.chuanwise.xiaoming.api.contact.message;

import cn.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.api.user.PrivateXiaomingUser;

public interface PrivateMessage extends Message {
    @Override
    PrivateXiaomingUser getSender();

    @Override
    PrivateContact getContact();

    @Override
    PrivateMessage clone() throws CloneNotSupportedException;
}
