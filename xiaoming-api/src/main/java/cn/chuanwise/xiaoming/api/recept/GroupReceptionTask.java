package cn.chuanwise.xiaoming.api.recept;

import cn.chuanwise.xiaoming.api.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.api.user.GroupXiaomingUser;

import java.util.List;

public interface GroupReceptionTask extends ReceptionTask {
    @Override
    GroupXiaomingUser getUser();

    @Override
    List<GroupMessage> getRecentMessages();
}
