package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

public interface PrivateMessage extends Message {
    @Override
    PrivateXiaomingUser getSender();

    @Override
    PrivateContact getContact();

    @Override
    PrivateMessage clone() throws CloneNotSupportedException;
}
