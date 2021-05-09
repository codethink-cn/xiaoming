package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;

public interface GroupXiaomingUser extends XiaomingObject, XiaomingUser {
    Friend getAsFriend();

    /**
     * 在所在的群中发送消息
     * @param message
     * @param arguments
     * @return
     */
    boolean sendGroupMessage(String message, Object... arguments);

    /**
     * 发送私聊消息
     * @param message
     * @param arguments
     * @return
     */
    boolean sendPrivateMessage(String message, Object... arguments);

    long getGroupNumber();

    Member getAsGroupMember();

    void setAsGroupMember(Member member);

    default ResponseGroup getResponseGroup() {
        return getXiaomingBot().getResponseGroupManager().fromCode(getGroupNumber());
    }

    @Override
    default String getName() {
        final Member member = getAsGroupMember();
        return "[" + member.getGroup().getName() + "(" + getGroupNumber() + ")] " + member.getNick() + "(" + member.getId() + ")";
    }
}
