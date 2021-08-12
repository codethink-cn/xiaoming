package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.language.Sentence;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.utility.InteractorUtility;
import cn.chuanwise.xiaoming.contact.message.Message;
import java.util.concurrent.ScheduledFuture;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.util.List;
import java.util.function.Function;

public interface XiaomingContact<M extends Message, MC extends Contact> extends XiaomingObject {
    String getCompleteName();

    MC getMiraiContact();

    default String getAliasAndCode() {
        return getAlias() + "（" + getCodeString() + "）";
    }

    default long getCode() {
        return getMiraiContact().getId();
    }

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getName();

    String getAlias();

    String getAvatarUrl();

    default M send(String message) {
        final String replacedMessage = getXiaomingBot().getLanguageManager().render(message);
        return send(MiraiCode.deserializeMiraiCode(replacedMessage));
    }

    M send(MessageChain messages);

    default M send(M messages) {
        messages.setOriginalMessageChain(getMiraiContact().sendMessage(messages.getMessageChain()).getSource().getOriginalMessage());
        return messages;
    }

    default M send(Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        return send(getXiaomingBot().getLanguageManager().render(sentence, externalGetter, arguments));
    }

    default ScheduledFuture<M> sendLater(long delay, Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> send(getXiaomingBot().getLanguageManager().render(sentence, externalGetter, arguments)));
    }

    default ScheduledFuture<M> sendLater(long delay, String message) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> send(message));
    }

    default ScheduledFuture<M> sendLater(long delay, MessageChain message) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> send(message));
    }

    default ScheduledFuture<M> sendLater(long delay, M message) {
        return getXiaomingBot().getScheduler().runLater(delay, () -> send(message));
    }

    default M reply(Message quote, MessageChain messages) {
        return send(new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(messages));
    }

    default M reply(Message quote, M message) {
        return reply(quote, message.getMessageChain());
    }

    default M reply(Message quote, String message) {
        return reply(quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().render(message)));
    }

    default ScheduledFuture<M> replyLater(long delay, Message quote, MessageChain messages) {
        return sendLater(delay, new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(messages));
    }

    default ScheduledFuture<M> replyLater(long delay, Message quote, String message) {
        return replyLater(delay, quote, MiraiCode.deserializeMiraiCode(getXiaomingBot().getLanguageManager().render(message)));
    }

    default ScheduledFuture<M> replyLater(long delay, Message quote, M message) {
        return replyLater(delay, quote, message.getMessageChain());
    }

    List<M> getRecentMessages();

    default M nextMessage(long timeout) {
        return InteractorUtility.waitLastElement(getRecentMessages(), timeout);
    }

    default M nextMessage() {
        return nextMessage(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime());
    }

    default Image uploadImage(ExternalResource resource) {
        return getMiraiContact().uploadImage(resource);
    }
}
