package cn.chuanwise.xiaoming.api.recept;

import cn.chuanwise.xiaoming.api.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.api.user.MemberXiaomingUser;

import java.util.List;

public interface MemberReceptionTask extends ReceptionTask {
    @Override
    MemberXiaomingUser getUser();

    @Override
    List<MemberMessage> getRecentMessages();
}
