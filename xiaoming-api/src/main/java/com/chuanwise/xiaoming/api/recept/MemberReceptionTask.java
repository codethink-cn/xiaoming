package com.chuanwise.xiaoming.api.recept;

import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;

import java.util.List;

public interface MemberReceptionTask extends ReceptionTask {
    @Override
    MemberXiaomingUser getUser();

    @Override
    List<MemberMessage> getRecentMessages();
}
