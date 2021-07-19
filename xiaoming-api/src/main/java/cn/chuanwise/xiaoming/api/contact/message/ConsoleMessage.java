package cn.chuanwise.xiaoming.api.contact.message;

import cn.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;

public interface ConsoleMessage extends Message {
    @Override
    ConsoleXiaomingUser getSender();

    @Override
    ConsoleContact getContact();

    @Override
    ConsoleMessage clone() throws CloneNotSupportedException;
}
