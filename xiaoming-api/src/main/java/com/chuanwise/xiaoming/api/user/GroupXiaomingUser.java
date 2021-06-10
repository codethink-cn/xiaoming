package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;

import java.util.List;

public interface GroupXiaomingUser extends XiaomingUser {
    @Override
    GroupContact getContact();

    TempContact getTempContact();

    default long getGroupCode() {
        return getContact().getCode();
    }

    default String getGroupCodeString() {
        return getContact().getCodeString();
    }

    @Override
    List<GroupMessage> getRecentMessages();

    default void onNextInput(GroupMessage message) {
        final List<GroupMessage> list = getRecentMessages();
        list.add(message);
        synchronized (list) {
            list.notifyAll();
        }
    }

    void setReceptionTask(GroupReceptionTask receptionTask);

    default ResponseGroup getResponseGroup() {
        return getContact().getResponseGroup();
    }

    @Override
    GroupReceptionTask getReceptionTask();
}
