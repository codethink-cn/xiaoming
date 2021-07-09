package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface MemberContact extends XiaomingContact<MemberMessage, NormalMember> {
    default Account getAccount() {
        return getXiaomingBot().getAccountManager().forAccount(getCode());
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

    default ResponseGroup getResponseGroup() {
        return getGroupContact().getResponseGroup();
    }

    default void mute(long timeMillis) {
        this.getMiraiContact().mute(((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis)));
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
        return replyGroup(quote, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default MemberMessage replyGroup(Message quote, MessageChain message) {
        return send(new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(message));
    }

    default ScheduableTask<MemberMessage> replyGroupLater(long delay, Message quote, String message) {
        return replyGroupLater(delay, quote, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<MemberMessage> replyGroupLater(long delay, Message quote, MessageChain message) {
        return sendLater(delay, new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(message));
    }

    default MemberXiaomingUser getUser() {
        return getXiaomingBot().getReceptionistManager().forReceptionist(getCode()).forMember(getGroupCode());
    }
}
