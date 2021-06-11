package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
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

    default ConsoleMessage atReply(ConsoleMessage quote, String message) {
        return atReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default ConsoleMessage atReply(ConsoleMessage quote, MessageChain message) {
        return reply(quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ConsoleMessage atReply(ConsoleMessage quote, ConsoleMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default AsyncResult<ConsoleMessage> atReplyLater(long delay, ConsoleMessage quote, String message) {
        return atReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(message));
    }

    default AsyncResult<ConsoleMessage> atReplyLater(long delay, ConsoleMessage quote, MessageChain message) {
        return replyLater(delay, quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default AsyncResult<ConsoleMessage> atReplyLater(long delay, ConsoleMessage quote, ConsoleMessage message) {
        return replyLater(delay, quote, message.getMessageChain());
    }
}
