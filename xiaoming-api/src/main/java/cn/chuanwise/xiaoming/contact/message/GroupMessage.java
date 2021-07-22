package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;

public interface GroupMessage extends Message {
    @Override
    GroupXiaomingUser getSender();

    @Override
    GroupContact getContact();

    @Override
    GroupMessage clone() throws CloneNotSupportedException;
}
