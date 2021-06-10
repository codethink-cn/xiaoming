package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

public interface ConsoleMessage extends Message {
    @Override
    ConsoleXiaomingUser getSender();

    @Override
    ConsoleContact getContact();
}
