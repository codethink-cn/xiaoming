package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;

import java.util.List;

public interface PrivateReceptionTask extends ReceptionTask {
    @Override
    default List<PrivateMessage> getRecentMessages() {
        return getUser().getRecentMessages();
    }

    @Override
    PrivateXiaomingUser getUser();
}
