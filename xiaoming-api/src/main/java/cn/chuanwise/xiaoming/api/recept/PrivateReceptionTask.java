package cn.chuanwise.xiaoming.api.recept;

import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.api.user.PrivateXiaomingUser;

import java.util.List;

public interface PrivateReceptionTask extends ReceptionTask {
    @Override
    default List<PrivateMessage> getRecentMessages() {
        return getUser().getRecentMessages();
    }

    @Override
    PrivateXiaomingUser getUser();
}
