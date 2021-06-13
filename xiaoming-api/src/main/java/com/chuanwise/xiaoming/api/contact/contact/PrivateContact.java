package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.permission.PermissionAccessible;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;

import java.util.List;
import java.util.Objects;

public interface PrivateContact extends XiaomingContact<PrivateMessage, Friend> {
    default Account getAccount() {
        return getXiaomingBot().getAccountManager().getAccount(getCode());
    }

    default Account getOrPutAccount() {
        return getXiaomingBot().getAccountManager().getOrPutAccount(getCode());
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getName() {
        return getMiraiContact().getNick();
    }

    @Override
    default String getAlias() {
        final Account account = getAccount();
        return Objects.nonNull(account) ? account.getAlias() : getName();
    }

    @Override
    default String getCompleteName() {
        return getAlias() + "(" + getCodeString() + ")";
    }

    default String getRemark() {
        return getMiraiContact().getRemark();
    }

    default void delete() {
        getMiraiContact().delete();
    }

    default void nudge() {
        getMiraiContact().nudge();
    }
}
