package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.List;

public interface ConsoleReceptionTask extends ReceptionTask {
    @Override
    ConsoleXiaomingUser getUser();

    @Override
    List<ConsoleMessage> getRecentMessages();
}
