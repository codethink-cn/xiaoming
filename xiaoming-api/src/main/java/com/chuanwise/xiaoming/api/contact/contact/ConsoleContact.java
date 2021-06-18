package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

public interface ConsoleContact extends XiaomingContact<ConsoleMessage, Friend> {
    @Override
    default Friend getMiraiContact() {
        return getXiaomingBot().getMiraiBot().getAsFriend();
    }

    @Override
    default String getName() {
        return "后台";
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getAlias() {
        return "后台";
    }

    @Override
    default String getCompleteName() {
        return "后台";
    }

    default ConsoleMessage atReply(Message quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ConsoleMessage atReply(Message quote, MessageChain message) {
        return reply(quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ConsoleMessage atReply(Message quote, ConsoleMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default ScheduableTask<ConsoleMessage> atReplyLater(long delay, Message quote, String message) {
        return atReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(ArgumentUtils.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<ConsoleMessage> atReplyLater(long delay, Message quote, MessageChain message) {
        return replyLater(delay, quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ScheduableTask<ConsoleMessage> atReplyLater(long delay, Message quote, ConsoleMessage message) {
        return replyLater(delay, quote, message.getMessageChain());
    }
}
