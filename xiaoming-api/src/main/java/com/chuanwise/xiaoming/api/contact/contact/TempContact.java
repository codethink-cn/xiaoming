package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface TempContact extends XiaomingContact<TempMessage, NormalMember> {
    default Account getOrPutAccount() {
        return getXiaomingBot().getAccountManager().getOrPutAccount(getCode());
    }

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
        return "[" + getGroupContact().getCompleteName() + "]" + getName() + "(" + getCodeString() + ")";
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

    default String getSpecialTitle() {
        return this.getMiraiContact().getSpecialTitle();
    }

    default int getJoinTime() {
        return this.getMiraiContact().getJoinTimestamp();
    }

    default int getLastSpeakTime() {
        return this.getMiraiContact().getLastSpeakTimestamp();
    }

    default int getRemainMuteTime() {
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

    default TempMessage replyGroup(GroupMessage quote, String message) {
        return replyGroup(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default TempMessage replyGroup(GroupMessage quote, MessageChain message) {
        return send(new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(message));
    }

    default AsyncResult<TempMessage> replyGroupLater(long delay, GroupMessage quote, String message) {
        return replyGroupLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default AsyncResult<TempMessage> replyGroupLater(long delay, GroupMessage quote, MessageChain message) {
        return sendLater(delay, new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(message));
    }
}
