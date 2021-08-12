package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.contact.message.Message;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.concurrent.ScheduledFuture;

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
//        return atReply(quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().render(message)));
        return null;
    }

    default ConsoleMessage atReply(Message quote, MessageChain message) {
        return reply(quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ConsoleMessage atReply(Message quote, ConsoleMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default ScheduledFuture<ConsoleMessage> atReplyLater(long delay, Message quote, String message) {
//        return atReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().render(message)));
        return null;
    }

    default ScheduledFuture<ConsoleMessage> atReplyLater(long delay, Message quote, MessageChain message) {
        return replyLater(delay, quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ScheduledFuture<ConsoleMessage> atReplyLater(long delay, Message quote, ConsoleMessage message) {
        return replyLater(delay, quote, message.getMessageChain());
    }
}
