package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.recept.TempReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.List;

public interface TempXiaomingUser extends XiaomingUser<TempContact, TempMessage, TempReceptionTask> {
    default ResponseGroup getResponseGroup() {
        return getContact().getResponseGroup();
    }

    void setReceptionTask(TempReceptionTask task);
}
