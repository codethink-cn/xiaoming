package cn.chuanwise.xiaoming.api.contact.contact;

import cn.chuanwise.xiaoming.api.account.Account;
import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import net.mamoe.mirai.contact.Friend;

import java.util.Objects;

public interface PrivateContact extends XiaomingContact<PrivateMessage, Friend> {
    default Account getAccount() {
        return getXiaomingBot().getAccountManager().forAccount(getCode());
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
        return getAlias() + "（" + getCodeString() + "）";
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

    default PrivateXiaomingUser getUser() {
        return getXiaomingBot().getReceptionistManager().forReceptionist(getCode()).forPrivate();
    }
}
