package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;

import java.util.List;

public interface MemberReceptionTask extends ReceptionTask {
    @Override
    MemberXiaomingUser getUser();

    @Override
    List<MemberMessage> getRecentMessages();
}
