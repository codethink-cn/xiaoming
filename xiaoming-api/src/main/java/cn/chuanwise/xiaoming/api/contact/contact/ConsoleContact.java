package cn.chuanwise.xiaoming.api.contact.contact;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

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
        return atReply(quote, MiraiCode.deserializeMiraiCode(ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ConsoleMessage atReply(Message quote, MessageChain message) {
        return reply(quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ConsoleMessage atReply(Message quote, ConsoleMessage message) {
        return atReply(quote, message.getMessageChain());
    }

    default ScheduableTask<ConsoleMessage> atReplyLater(long delay, Message quote, String message) {
        return atReplyLater(delay, quote, MiraiCode.deserializeMiraiCode(ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<ConsoleMessage> atReplyLater(long delay, Message quote, MessageChain message) {
        return replyLater(delay, quote, quote.getSender().getAt().plus(" ").plus(message));
    }

    default ScheduableTask<ConsoleMessage> atReplyLater(long delay, Message quote, ConsoleMessage message) {
        return replyLater(delay, quote, message.getMessageChain());
    }
}
