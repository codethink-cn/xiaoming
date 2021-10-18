package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface MemberContact extends XiaomingContact<NormalMember> {
    @Override
    default Optional<Message> nextMessage(long timeout) throws InterruptedException {
        return getXiaomingBot()
                .getContactManager()
                .nextMemberMessage(getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    default Account getAccount() {
        return getXiaomingBot().getAccountManager().createAccount(getCode());
    }

    @Override
    default String getName() {
        return this.getMiraiContact().getNick();
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getAliasAndCode() {
        return "「" + getGroupContact().getAliasAndCode() + "」" + getName() + "（" + getCodeString() + "）";
    }

    @Override
    default String getAlias() {
        final Account account = getAccount();
        return Objects.nonNull(account) && Objects.nonNull(account.getAlias()) ? account.getAlias() : getName();
    }

    default String getNick() {
        return this.getMiraiContact().getNick();
    }

    default String getNameCard() {
        return this.getMiraiContact().getNameCard();
    }

    default MemberPermission getPermission() {
        return this.getMiraiContact().getPermission();
    }

    default GroupInformation getGroupRecord() {
        return getGroupContact().getGroupInformation();
    }

    default void mute(long timeMillis) {
        getMiraiContact().mute(((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis)));
    }

    default void unmute() {
        this.getMiraiContact().unmute();
    }

    default void nudge() {
        this.getMiraiContact().nudge();
    }

    GroupContact getGroupContact();

    default long getGroupCode() {
        return getGroupContact().getCode();
    }

    default String getGroupCodeString() {
        return getGroupContact().getCodeString();
    }

    default String getSpecialTitle() {
        return this.getMiraiContact().getSpecialTitle();
    }

    default long getJoinTime() {
        return this.getMiraiContact().getJoinTimestamp();
    }

    default long getLastSpeakTime() {
        return this.getMiraiContact().getLastSpeakTimestamp();
    }

    default long getRemainMuteTime() {
        return this.getMiraiContact().getMuteTimeRemaining();
    }

    default boolean isMuted() {
        return this.getMiraiContact().isMuted();
    }

    default void kick(String reason) {
        this.getMiraiContact().kick(reason);
    }

    default void setNameCard(String nameCard) {
        this.getMiraiContact().setNameCard(nameCard);
    }

    default void setSpecialTitle(String specialTitle) {
        this.getMiraiContact().setSpecialTitle(specialTitle);
    }

    default UserProfile getUserProfile() {
        return this.getMiraiContact().queryProfile();
    }

    default MemberXiaomingUser getUser() {
        return getXiaomingBot().getReceptionistManager().getReceptionist(getCode()).getMemberXiaomingUser(getGroupCode());
    }

    @Override
    default Set<String> getTags() {
        return getXiaomingBot().getAccountManager().getTags(getCode());
    }
}
