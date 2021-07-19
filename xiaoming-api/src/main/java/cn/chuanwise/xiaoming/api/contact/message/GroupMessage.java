package cn.chuanwise.xiaoming.api.contact.message;

import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.user.GroupXiaomingUser;

public interface GroupMessage extends Message {
    @Override
    GroupXiaomingUser getSender();

    @Override
    GroupContact getContact();

    @Override
    GroupMessage clone() throws CloneNotSupportedException;
}
