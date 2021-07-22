package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;

import java.util.List;

public interface ConsoleReceptionTask extends ReceptionTask {
    @Override
    ConsoleXiaomingUser getUser();

    @Override
    List<ConsoleMessage> getRecentMessages();
}
