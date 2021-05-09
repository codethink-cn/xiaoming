package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl implements ConsoleXiaomingUser {
    long qq;
    long group;
    Member member;
    String message;
    UserInteractRunnable userInteractRunnable;
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public long getQQ() {
        return qq;
    }

    @Override
    public Friend getAsFriend() {
        return getFriend();
    }

    @Override
    public boolean sendGroupMessage(String message, Object... arguments) {
        getLog().info("（群 " + getGroupNumber() + " 的消息）" + message, arguments);
        return true;
    }

    @Override
    public boolean sendPrivateMessage(String message, Object... arguments) {
        getLog().info("（私聊消息）" + message, arguments);
        return true;
    }

    @Override
    public Friend getFriend() {
        return getXiaomingBot().getMiraiBot().getFriend(qq);
    }

    @Override
    public void setFriend(Friend friend) {
        qq = friend.getId();
    }

    @Override
    public long getGroupNumber() {
        return group;
    }

    @Override
    public Member getAsGroupMember() { 
        return member;
    }

    @Override
    public void setAsGroupMember(Member member) {
        this.member = member;
    }
}
