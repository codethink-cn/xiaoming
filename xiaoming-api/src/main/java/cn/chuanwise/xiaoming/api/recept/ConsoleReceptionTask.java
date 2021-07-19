package cn.chuanwise.xiaoming.api.recept;

import cn.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.api.contact.message.ConsoleMessage;

import java.util.List;

public interface ConsoleReceptionTask extends ReceptionTask {
    @Override
    ConsoleXiaomingUser getUser();

    @Override
    List<ConsoleMessage> getRecentMessages();
}
