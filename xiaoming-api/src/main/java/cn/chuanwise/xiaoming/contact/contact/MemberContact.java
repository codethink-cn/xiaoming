package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface MemberContact extends XiaomingContact<MemberMessage, NormalMember> {
    default Account getAccount() {
        return getXiaomingBot().getAccountManager().getAccount(getCode());
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
    default String getCompleteName() {
        return "「" + getGroupContact().getCompleteName() + "」" + getName() + "（" + getCodeString() + "）";
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

    default GroupRecord getGroupRecord() {
        return getGroupContact().getGroupRecord();
    }

    default void mute(long timeMillis) {
        getMiraiContact().mute(((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis)));
    }

    default void lift() {
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

    default MemberMessage replyGroup(Message quote, String message) {
        return replyGroup(quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }

    default MemberMessage replyGroup(Message quote, MessageChain message) {
        return send(new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(message));
    }

    default ScheduledFuture<MemberMessage> replyGroupLater(long delay, Message quote, String message) {
        return replyGroupLater(delay, quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().format(message)));
    }

    default ScheduledFuture<MemberMessage> replyGroupLater(long delay, Message quote, MessageChain message) {
        return sendLater(delay, new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(message));
    }

    default MemberXiaomingUser getUser() {
        return getXiaomingBot().getReceptionistManager().getReceptionist(getCode()).forMember(getGroupCode());
    }

    @Override
    default Set<String> getTags() {
        return getXiaomingBot().getAccountManager().getTags(getCode());
    }
}
