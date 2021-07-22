package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;

public interface PrivateMessage extends Message {
    @Override
    PrivateXiaomingUser getSender();

    @Override
    PrivateContact getContact();

    @Override
    PrivateMessage clone() throws CloneNotSupportedException;
}
