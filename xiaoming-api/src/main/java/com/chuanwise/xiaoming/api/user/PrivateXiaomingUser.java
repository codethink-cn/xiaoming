package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

public interface PrivateXiaomingUser extends XiaomingObject, XiaomingUser {
    boolean sendPrivateMessage(String message, Object... arguments);

    Friend getFriend();

    void setFriend(Friend friend);

    @Override
    default String getName() {
        return getFriend().getNick() + "(" + getFriend().getId() + ")";
    }
}
