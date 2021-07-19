package cn.chuanwise.xiaoming.api.user;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.api.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.api.contact.message.MemberMessage;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import cn.chuanwise.xiaoming.api.group.GroupRecord;
import cn.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
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

    default GroupRecord getGroupRecord() {
        return getContact().getGroupRecord();
    }

    default GroupMessage atReply(Message quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default GroupMessage atReply(Message quote, GroupMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default GroupMessage atReply(Message quote, MessageChain message) {
        return getContact().atReply(quote, message);
    }

    default GroupMessage sendGroupMessage(GroupMessage message) {
        return sendGroupMessage(message.getMessageChain());
    }

    default GroupMessage sendGroupMessage(MessageChain message) {
        return getContact().send(message);
    }

    default GroupMessage sendGroupMessage(String message) {
        return sendGroupMessage(MiraiCode.deserializeMiraiCode(ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<GroupMessage> atReplyLater(long delay, Message quote, MessageChain message) {
        return getContact().atReplayLater(delay, quote, message);
    }

    default ScheduableTask<GroupMessage> atReplyLater(long delay, Message quote, GroupMessage message) {
        return atReplyLater(delay, quote, message.getMessageChain());
    }

    default ScheduableTask<GroupMessage> atReplyLater(long delay, Message quote, String message) {
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

    @Override
    default MemberMessage sendPrivateMessage(String message, Object... arguments) {
        return getMemberContact().send(MiraiCode.deserializeMiraiCode(replaceArguments(message, arguments)));
    }
}
