package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

public interface TempMessage extends Message {
    @Override
    TempXiaomingUser getSender();

    @Override
    TempContact getContact();

    @Override
    TempMessage clone() throws CloneNotSupportedException;
}
