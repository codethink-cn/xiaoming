package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

public interface TempXiaomingUser extends XiaomingObject, XiaomingUser {
    Friend getAsFriend();

    boolean sendGroupMessage(String message, Object... arguments);

    boolean sendPrivateMessage(String message, Object... arguments);

    long getGroupNumber();

    Member getAsGroupMember();

    void setAsGroupMember(Member member);

    @Override
    default String getName() {
        final Member member = getAsGroupMember();
        return "[" + member.getGroup().getName() + "(" + getGroupNumber() + ")] " + member.getNick() + "(" + member.getId() + ")";
    }

}
