package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.List;

public interface TempReceptionTask extends ReceptionTask {
    @Override
    TempXiaomingUser getUser();

    @Override
    List<TempMessage> getRecentMessages();
}
