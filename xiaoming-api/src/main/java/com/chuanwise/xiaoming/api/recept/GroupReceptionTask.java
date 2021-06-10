package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.recept.ReceptionTask;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;

import java.util.List;

public interface GroupReceptionTask extends ReceptionTask {
    @Override
    GroupXiaomingUser getUser();

    @Override
    List<GroupMessage> getRecentMessages();
}
