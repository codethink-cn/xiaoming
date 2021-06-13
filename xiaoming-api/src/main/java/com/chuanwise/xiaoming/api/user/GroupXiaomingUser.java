package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

public interface GroupXiaomingUser extends XiaomingUser<GroupContact, GroupMessage, GroupReceptionTask> {
    MemberContact getMemberContact();

    default long getGroupCode() {
        return getContact().getCode();
    }

    default String getGroupCodeString() {
        return getContact().getCodeString();
    }

    void setReceptionTask(GroupReceptionTask receptionTask);

    default ResponseGroup getResponseGroup() {
        return getContact().getResponseGroup();
    }

    default GroupMessage atReply(GroupMessage quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default GroupMessage atReply(GroupMessage quote, GroupMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default GroupMessage atReply(GroupMessage quote, MessageChain message) {
        return getContact().atReply(quote, message);
    }

    default GroupMessage sendGroupMessage(GroupMessage message) {
        return sendGroupMessage(message.getMessageChain());
    }

    default GroupMessage sendGroupMessage(MessageChain message) {
        return getContact().send(message);
    }

    default GroupMessage sendGroupMessage(String message) {
        return sendGroupMessage(MiraiCode.deserializeMiraiCode(message));
    }

    default ScheduableTask<GroupMessage> atReplyLater(long delay, GroupMessage quote, MessageChain message) {
        return getContact().atReplayLater(delay, quote, message);
    }

    default ScheduableTask<GroupMessage> atReplyLater(long delay, GroupMessage quote, GroupMessage message) {
        return atReplyLater(delay, quote, message.getMessageChain());
    }

    default ScheduableTask<GroupMessage> atReplyLater(long delay, GroupMessage quote, String message) {
        return atReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default GroupMessage atReplyLatest(String message) {
        return atReply(getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }

    default GroupMessage atReplyLatest(GroupMessage message) {
        return atReply(getLatestMessage(), message.getMessageChain());
    }

    default GroupMessage atReplyLatest(MessageChain message) {
        return getContact().atReply(getLatestMessage(), message);
    }

    default ScheduableTask<GroupMessage> atReplyLatestLater(long delay, MessageChain message) {
        return getContact().atReplayLater(delay, getLatestMessage(), message);
    }

    default ScheduableTask<GroupMessage> atReplyLatestLater(long delay, GroupMessage message) {
        return atReplyLatestLater(delay, message.getMessageChain());
    }

    default ScheduableTask<GroupMessage> atReplyLatestLater(long delay, String message) {
        return atReplyLatestLater(delay, MiraiCode.deserializeMiraiCode(message));
    }

    default MemberMessage privateReply(GroupMessage quote, String message) {
        return privateReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default MemberMessage privateReply(GroupMessage quote, GroupMessage message) {
        return privateReply(quote, message.getMessageChain());
    }

    default MemberMessage privateReply(GroupMessage quote, MessageChain message) {
        return getMemberContact().replyGroup(quote, message);
    }

    default ScheduableTask<MemberMessage> privateReplyLater(long delay, GroupMessage quote, MessageChain message) {
        return getMemberContact().replyGroupLater(delay, quote, message);
    }

    default ScheduableTask<MemberMessage> privateReplyLater(long delay, GroupMessage quote, GroupMessage message) {
        return privateReplyLater(delay, quote, message.getMessageChain());
    }

    default ScheduableTask<MemberMessage> privateReplyLater(long delay, GroupMessage quote, String message) {
        return privateReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default MemberMessage privateReplyLaterLatest(String message) {
        return privateReply(getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }

    default MemberMessage privateReplyLaterLatest(GroupMessage message) {
        return privateReply(getLatestMessage(), message.getMessageChain());
    }

    default MemberMessage privateReplyLaterLatest(MessageChain message) {
        return privateReply(getLatestMessage(), message);
    }

    default ScheduableTask<MemberMessage> privateReplyLatestLater(long delay, MessageChain message) {
        return privateReplyLater(delay, getLatestMessage(), message);
    }

    default ScheduableTask<MemberMessage> privateReplyLatestLater(long delay, GroupMessage message) {
        return privateReplyLater(delay, getLatestMessage(), message.getMessageChain());
    }

    default ScheduableTask<MemberMessage> privateReplyLatestLater(long delay, String message) {
        return privateReplyLater(delay, getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }

    @Override
    default void nudge() {
        getMemberContact().nudge();
    }

    default void mute(long timeMillis) {
        getMemberContact().mute(timeMillis);
    }

    default void lift() {
        getMemberContact().lift();
    }
}
