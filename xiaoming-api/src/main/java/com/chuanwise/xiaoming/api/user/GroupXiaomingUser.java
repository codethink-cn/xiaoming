package com.chuanwise.xiaoming.api.user;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

public interface GroupXiaomingUser extends XiaomingUser<GroupContact, GroupMessage, GroupReceptionTask> {
    TempContact getTempContact();

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

    default AsyncResult<GroupMessage> atReplyLater(long delay, GroupMessage quote, MessageChain message) {
        return getContact().atReplayLater(delay, quote, message);
    }

    default AsyncResult<GroupMessage> atReplyLater(long delay, GroupMessage quote, GroupMessage message) {
        return atReplyLater(delay, quote, message.getMessageChain());
    }

    default AsyncResult<GroupMessage> atReplyLater(long delay, GroupMessage quote, String message) {
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

    default AsyncResult<GroupMessage> atReplyLatestLater(long delay, MessageChain message) {
        return getContact().atReplayLater(delay, getLatestMessage(), message);
    }

    default AsyncResult<GroupMessage> atReplyLatestLater(long delay, GroupMessage message) {
        return atReplyLatestLater(delay, message.getMessageChain());
    }

    default AsyncResult<GroupMessage> atReplyLatestLater(long delay, String message) {
        return atReplyLatestLater(delay, MiraiCode.deserializeMiraiCode(message));
    }

    default TempMessage privateReply(GroupMessage quote, String message) {
        return privateReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default TempMessage privateReply(GroupMessage quote, GroupMessage message) {
        return privateReply(quote, message.getMessageChain());
    }

    default TempMessage privateReply(GroupMessage quote, MessageChain message) {
        return getTempContact().replyGroup(quote, message);
    }

    default AsyncResult<TempMessage> privateReplyLater(long delay, GroupMessage quote, MessageChain message) {
        return getTempContact().replyGroupLater(delay, quote, message);
    }

    default AsyncResult<TempMessage> privateReplyLater(long delay, GroupMessage quote, GroupMessage message) {
        return privateReplyLater(delay, quote, message.getMessageChain());
    }

    default AsyncResult<TempMessage> privateReplyLater(long delay, GroupMessage quote, String message) {
        return privateReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default TempMessage privateReplyLaterLatest(String message) {
        return privateReply(getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }

    default TempMessage privateReplyLaterLatest(GroupMessage message) {
        return privateReply(getLatestMessage(), message.getMessageChain());
    }

    default TempMessage privateReplyLaterLatest(MessageChain message) {
        return privateReply(getLatestMessage(), message);
    }

    default AsyncResult<TempMessage> privateReplyLatestLater(long delay, MessageChain message) {
        return privateReplyLater(delay, getLatestMessage(), message);
    }

    default AsyncResult<TempMessage> privateReplyLatestLater(long delay, GroupMessage message) {
        return privateReplyLater(delay, getLatestMessage(), message.getMessageChain());
    }

    default AsyncResult<TempMessage> privateReplyLatestLater(long delay, String message) {
        return privateReplyLater(delay, getLatestMessage(), MiraiCode.deserializeMiraiCode(message));
    }
}
