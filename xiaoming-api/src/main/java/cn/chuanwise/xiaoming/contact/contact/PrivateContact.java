package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import net.mamoe.mirai.contact.Friend;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public interface PrivateContact extends XiaomingContact<Friend> {
    @Override
    default Optional<Message> nextMessage(long timeout) throws InterruptedException {
        return getXiaomingBot()
                .getContactManager()
                .nextPrivateMessage(getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    default Account getAccount() {
        return getXiaomingBot().getAccountManager().createAccount(getCode());
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
    default String getAliasAndCode() {
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
        return getXiaomingBot().getReceptionistManager().getReceptionist(getCode()).getPrivateXiaomingUser().orElseThrow();
    }

    @Override
    default Set<String> getTags() {
        return getXiaomingBot().getAccountManager().getTags(getCode());
    }
}
