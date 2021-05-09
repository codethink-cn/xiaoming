package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

public interface UserInteractor extends XiaomingObject {
    void onGroupMessage(Member member, String message);

    void onTempMessage(Member member, String message);

    void onPrivateMessage(Friend friend, String message);

    void setUser(XiaomingUser user);

    TempXiaomingUser getTempXiaomingUser();

    PrivateXiaomingUser getPrivateXiaomingUser();

    GroupXiaomingUser getGroupXiaomingUser();

    UserInteractRunnable getUserInteractRunnable();
}