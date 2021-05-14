package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.user.Receiptionist;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 小明的使用者对象
 * @author Chuanwise
 */
@Getter
public class XiaomingUserImpl extends HostObjectImpl implements XiaomingUser {
    public XiaomingUserImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    /**
     * 当前消息
     */
    String message;

    @Override
    public void setMessage(String message) {
        if (Objects.nonNull(message)) {
            recentInputs.add(message);
        }
        this.message = message;
    }

    /**
     * 发送消息的缓存机制
     */
    StringBuilder buffer = new StringBuilder();

    @Setter
    boolean usingBuffer;

    /**
     * 当前输入
     */
    List<String> recentInputs = new ArrayList<>();

    @Override
    public void clearRecentInputs() {
        if (!recentInputs.isEmpty()) {
            recentInputs = new ArrayList<>();
        }
    }

    /**
     * 招待员
     */
    Receiptionist receptionist = new ReceptionistImpl(this);

    /**
     * 该用户的多重身份
     */
    Member asGroupMember, asTempMember;

    @Override
    public void setAsTempMember(Member asTempMember) {
        this.asTempMember = asTempMember;
        asPrivate = null;
        asGroupMember = null;
    }

    @Override
    public void setAsGroupMember(Member asGroupMember) {
        this.asGroupMember = asGroupMember;
        asTempMember = null;
        asPrivate = null;
    }

    Friend asPrivate;

    @Override
    public void setAsPrivate(Friend asPrivate) {
        this.asPrivate = asPrivate;
        asTempMember = null;
        asGroupMember = null;
    }

    @Override
    public long getQQ() {
        if (inGroup()) {
            return asGroupMember.getId();
        } else if (inTemp()) {
            return asTempMember.getId();
        } else {
            return asPrivate.getId();
        }
    }

    @Override
    public String getName() {
        final Account account = getAccount();
        if (Objects.nonNull(account) && Objects.nonNull(account.getAlias())) {
            return account.getAlias();
        } else if (inGroup()) {
            return asGroupMember.getNick();
        } else if (inTemp()) {
            return asTempMember.getNick();
        } else {
            return asPrivate.getNick();
        }
    }

    @Override
    public String getCompleteName() {
        if (inGroup()) {
            final Group group = asGroupMember.getGroup();
            return "[" + group.getName() + "(" + group.getId() + ")] " + getName() + "(" + getQQ() +")";
        } else if (inTemp()) {
            final Group group = asTempMember.getGroup();
            return getName() + "(" + getQQ() +")" + " 来自 [" + group.getName() + "(" + group.getId() + ")]";
        } else {
            return getName() + "(" + getQQ() +")";
        }
    }
}