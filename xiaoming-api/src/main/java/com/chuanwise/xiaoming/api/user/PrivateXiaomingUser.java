package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.recept.PrivateReceptionTask;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.List;

public interface PrivateXiaomingUser extends XiaomingUser<PrivateContact, PrivateMessage, PrivateReceptionTask> {
    void setReceptionTask(PrivateReceptionTask receptionTask);

    @Override
    default void nudge() {
        getContact().nudge();
    }
}