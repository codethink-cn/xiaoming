package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;

import java.util.List;

public interface GroupReceptionTask extends ReceptionTask {
    @Override
    GroupXiaomingUser getUser();

    @Override
    List<GroupMessage> getRecentMessages();
}
