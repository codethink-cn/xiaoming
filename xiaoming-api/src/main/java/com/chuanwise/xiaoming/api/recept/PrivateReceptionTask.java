package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;

import java.util.List;

public interface PrivateReceptionTask extends ReceptionTask {
    @Override
    default List<PrivateMessage> getRecentMessages() {
        return getUser().getRecentMessages();
    }

    @Override
    PrivateXiaomingUser getUser();
}
