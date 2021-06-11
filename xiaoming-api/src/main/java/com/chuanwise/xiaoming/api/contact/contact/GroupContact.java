package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.contact.ContactManager;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface GroupContact extends XiaomingContact<GroupMessage, Group> {
    default void atSend(long qq, String message) {
        atSend(qq, MiraiCode.deserializeMiraiCode(message));
    }

    default void atSend(long qq, MessageChain messages) {
        send(new At(qq).plus(" ").plus(messages));
    }

    default void atSend(long qq, Message messages) {
        atSend(qq, messages.getMessageChain());
    }

    default void atSendLater(long delay, long qq, String message) {
        sendLater(delay, new At(qq).serializeToMiraiCode() + " " + message);
    }

    default void atSendLater(long delay, long qq, MessageChain messages) {
        messages.add(0, new At(qq));
        sendLater(delay, messages);
    }

    default void atSendLater(long delay, long qq, Message messages) {
        atSendLater(delay, qq, messages.getMessageChain());
    }

    default GroupMessage atReply(GroupMessage quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default GroupMessage atReply(GroupMessage quote, MessageChain message) {
        return reply(quote, quote.getSender().getAt().plus(message));
    }

    default GroupMessage atReply(GroupMessage quote, GroupMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default AsyncResult<GroupMessage> atReplayLater(long delay, GroupMessage quote, MessageChain message) {
        return replyLater(delay, quote, quote.getSender().getAt().plus(message));
    }

    default AsyncResult<GroupMessage> atReplayLater(long delay, GroupMessage quote, String message) {
        return atReplayLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default AsyncResult<GroupMessage> atReplayLater(long delay, GroupMessage quote, GroupMessage message) {
        return atReplayLater(delay, quote, message.getMessageChain());
    }

    default ResponseGroup getResponseGroup() {
        return getXiaomingBot().getResponseGroupManager().forCode(getCode());
    }

    default Set<String> getTags() {
        return getXiaomingBot().getResponseGroupManager().getTags(getCode());
    }

    @Override
    default String getCompleteName() {
        return getName() + "(" + getCodeString() + ")";
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getName() {
        return getMiraiContact().getName();
    }

    @Override
    default String getAlias() {
        final ResponseGroup responseGroup = getResponseGroup();
        return Objects.nonNull(responseGroup) ? responseGroup.getAlias() : getName();
    }

    /**
     * 获得群成员信息
     * @param qq 群成员 QQ
     * @return 群成员信息。如果没有找到，返回 null
     */
    default TempContact getMember(long qq) {
        return getXiaomingBot().getContactManager().getTempContact(getCode(), qq);
    }

    default boolean quit() {
        return getMiraiContact().quit();
    }
}
