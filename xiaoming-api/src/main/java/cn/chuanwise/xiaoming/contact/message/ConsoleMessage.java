package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;

public interface ConsoleMessage extends Message {
    @Override
    ConsoleXiaomingUser getSender();

    @Override
    ConsoleContact getContact();

    @Override
    ConsoleMessage clone() throws CloneNotSupportedException;
}
