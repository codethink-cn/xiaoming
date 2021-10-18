package cn.chuanwise.xiaoming.message;

import cn.chuanwise.util.ArrayUtil;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.object.FormatableObject;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.util.MiraiCodeUtil;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;

public interface MessageSendable<M> extends XiaomingObject, FormatableObject {
    default M sendMessage(String miraiCode, Object... contexts) {
        return sendMessage(MiraiCode.deserializeMiraiCode(format(miraiCode, contexts)));
    }

    default M sendMessage(Sentence sentence, Object... contexts) {
        return sendMessage(format(sentence, contexts));
    }

    default M sendMessage(SingleMessage... elements) {
        return sendMessage(MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyMessage(Message quote, String message, Object... contexts) {
        return replyMessage(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyMessage(Message quote, Sentence sentence, Object... contexts) {
        return replyMessage(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(sentence, contexts)));
    }

    default M replyMessage(Message quote, SingleMessage... elements) {
        return replyMessage(quote.getOriginalMessageChain(), MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyMessage(Message quote, MessageChain messageChain) {
        return replyMessage(quote.getOriginalMessageChain(), messageChain);
    }

    default M replyMessage(MessageChain quote, String message, Object... contexts) {
        return replyMessage(quote, MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyMessage(MessageChain quote, Sentence sentence, Object... contexts) {
        return replyMessage(quote, MiraiCode.deserializeMiraiCode(format(sentence, contexts)));
    }

    default M replyMessage(MessageChain quote, SingleMessage... elements) {
        return replyMessage(quote, MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyMessage(MessageChain quote, MessageChain messageChain) {
        return sendMessage(new QuoteReply(quote).plus(messageChain));
    }

    M sendMessage(MessageChain messageChain);


    default M sendWarning(String miraiCode, Object... contexts) {
        return sendMessage(getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " " + miraiCode, contexts);
    }

    default M sendWarning(Sentence sentence, Object... contexts) {
        return sendWarning(format(sentence, contexts));
    }

    default M sendWarning(SingleMessage firstElement, SingleMessage... remainElements) {
        final SingleMessage[] singleMessages = ArrayUtil.insert(remainElements, 0, firstElement);
        return sendWarning(MiraiCodeUtil.asMessageChain(singleMessages));
    }

    default M sendWarning(MessageChain messageChain) {
        return sendWarning(messageChain.serializeToMiraiCode());
    }

    default M replyWarning(Message quote, String message, Object... contexts) {
        return replyWarning(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyWarning(Message quote, Sentence sentence, Object... contexts) {
        return replyWarning(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(sentence, contexts)));
    }

    default M replyWarning(Message quote, SingleMessage... elements) {
        return replyWarning(quote.getOriginalMessageChain(), MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyWarning(Message quote, MessageChain messageChain) {
        return replyWarning(quote.getOriginalMessageChain(), messageChain);
    }

    default M replyWarning(MessageChain quote, String message, Object... contexts) {
        return replyWarning(quote, MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyWarning(MessageChain quote, Sentence sentence, Object... contexts) {
        return replyWarning(quote, MiraiCode.deserializeMiraiCode(format(sentence, contexts)));
    }

    default M replyWarning(MessageChain quote, SingleMessage... elements) {
        return replyWarning(quote, MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyWarning(MessageChain quote, MessageChain messageChain) {
        return sendMessage(new QuoteReply(quote).plus(getXiaomingBot().getLanguageManager().getSentenceValue("warning") + " ").plus(messageChain));
    }



    default M sendError(String miraiCode, Object... contexts) {
        return sendMessage(getXiaomingBot().getLanguageManager().getSentenceValue("error") + " " + miraiCode, contexts);
    }

    default M sendError(Sentence sentence, Object... contexts) {
        return sendError(format(sentence, contexts));
    }

    default M sendError(SingleMessage firstElement, SingleMessage... remainElements) {
        final SingleMessage[] singleMessages = ArrayUtil.insert(remainElements, 0, firstElement);
        return sendError(MiraiCodeUtil.asMessageChain(singleMessages));
    }

    default M sendError(MessageChain messageChain) {
        return sendError(messageChain.serializeToMiraiCode());
    }

    default M replyError(Message quote, String message, Object... contexts) {
        return replyError(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyError(Message quote, Sentence sentence, Object... contexts) {
        return replyError(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(sentence, contexts)));
    }

    default M replyError(Message quote, SingleMessage... elements) {
        return replyError(quote.getOriginalMessageChain(), MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyError(Message quote, MessageChain messageChain) {
        return replyError(quote.getOriginalMessageChain(), messageChain);
    }

    default M replyError(MessageChain quote, String message, Object... contexts) {
        return replyError(quote, MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyError(MessageChain quote, Sentence sentence, Object... contexts) {
        return replyError(quote, MiraiCode.deserializeMiraiCode(format(sentence, contexts)));
    }

    default M replyError(MessageChain quote, SingleMessage... elements) {
        return replyError(quote, MiraiCodeUtil.asMessageChain(elements));
    }

    default M replyError(MessageChain quote, MessageChain messageChain) {
        return sendMessage(new QuoteReply(quote).plus(getXiaomingBot().getLanguageManager().getSentenceValue("error") + " ").plus(messageChain));
    }
}
