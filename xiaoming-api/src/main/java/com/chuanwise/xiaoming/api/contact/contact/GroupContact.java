package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.contact.ContactManager;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import com.chuanwise.xiaoming.api.util.MessageChainUtils;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface GroupContact extends XiaomingContact<GroupMessage, Group> {
    default GroupMessage atSend(long qq, String message) {
        return atSend(qq, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default GroupMessage atSend(long qq, MessageChain messages) {
        return send(new At(qq).plus(" ").plus(messages));
    }

    default GroupMessage atSend(long qq, Message messages) {
        return atSend(qq, messages.getMessageChain());
    }

    default AsyncResult<GroupMessage> atSendLater(long delay, long qq, String message) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> send(message));
    }

    default AsyncResult<GroupMessage> atSendLater(long delay, long qq, MessageChain messages) {
        messages.add(0, new At(qq));
        return sendLater(delay, messages);
    }

    default AsyncResult<GroupMessage> atSendLater(long delay, long qq, Message messages) {
        return atSendLater(delay, qq, messages.getMessageChain());
    }

    default GroupMessage atReply(Message quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default GroupMessage atReply(Message quote, MessageChain message) {
        return reply(quote, quote.getSender().getAt().plus(message));
    }

    default GroupMessage atReply(Message quote, GroupMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default ScheduableTask<GroupMessage> atReplayLater(long delay, Message quote, MessageChain message) {
        return replyLater(delay, quote, quote.getSender().getAt().plus(message));
    }

    default ScheduableTask<GroupMessage> atReplayLater(long delay, Message quote, String message) {
        return atReplayLater(delay, quote, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<GroupMessage> atReplayLater(long delay, Message quote, GroupMessage message) {
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
        return getName() + "（" + getCodeString() + "）";
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
    default MemberContact getMember(long qq) {
        return getXiaomingBot().getContactManager().getMemberContact(getCode(), qq);
    }

    default MemberContact getBotMember() {
        return getXiaomingBot().getContactManager().getMemberContact(this, getMiraiContact().getBotAsMember());
    }

    default MemberContact getOwner() {
        return getXiaomingBot().getContactManager().getMemberContact(this, getMiraiContact().getOwner());
    }

    default List<MemberContact> getMembers() {
        final ContactList<NormalMember> members = getMiraiContact().getMembers();
        final List<MemberContact> memberContacts = new ArrayList<>(members.size());
        final ContactManager contactManager = getXiaomingBot().getContactManager();

        for (NormalMember member : members) {
            memberContacts.add(contactManager.getMemberContact(this, member));
        }
        return memberContacts;
    }

    default boolean quit() {
        return getMiraiContact().quit();
    }

    default void setName(String name) {
        getMiraiContact().setName(name);
    }

    default GroupSettings getSettings() {
        return getMiraiContact().getSettings();
    }
}
