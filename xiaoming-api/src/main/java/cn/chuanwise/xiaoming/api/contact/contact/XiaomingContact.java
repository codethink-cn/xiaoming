package cn.chuanwise.xiaoming.api.contact.contact;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.xiaoming.api.object.XiaomingObject;
import cn.chuanwise.xiaoming.api.utility.InteractorUtils;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.util.List;

public interface XiaomingContact<M extends Message, MC extends Contact> extends XiaomingObject {
    String getCompleteName();

    MC getMiraiContact();

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
        final String replacedMessage = ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime());
        return send(MiraiCode.deserializeMiraiCode(replacedMessage));
    }

    M send(MessageChain messages);

    default M send(M messages) {
        messages.setOriginalMessageChain(getMiraiContact().sendMessage(messages.getMessageChain()).getSource().getOriginalMessage());
        return messages;
    }

    default ScheduableTask<M> sendLater(long timeout, String message) {
        return getXiaomingBot().getScheduler().run(() -> {
            try {
                Thread.sleep(timeout);
                return send(message);
            } catch (InterruptedException ignored) {
                return null;
            }
        });
    }

    default ScheduableTask<M> sendLater(long timeout, MessageChain message) {
        return getXiaomingBot().getScheduler().run(() -> {
            try {
                Thread.sleep(timeout);
                return send(message);
            } catch (InterruptedException ignored) {
                return null;
            }
        });
    }

    default ScheduableTask<M> sendLater(long timeout, M message) {
        return getXiaomingBot().getScheduler().run(() -> {
            try {
                Thread.sleep(timeout);
                return send(message);
            } catch (InterruptedException ignored) {
                return null;
            }
        });
    }

    default M reply(Message quote, MessageChain messages) {
        return send(new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(messages));
    }

    default M reply(Message quote, M message) {
        return reply(quote, message.getMessageChain());
    }

    default M reply(Message quote, String message) {
        return reply(quote, MiraiCode.deserializeMiraiCode(ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<M> replyLater(long delay, Message quote, MessageChain messages) {
        return sendLater(delay, new QuoteReply(quote.getOriginalMessageChain()).plus(" ").plus(messages));
    }

    default ScheduableTask<M> replyLater(long delay, Message quote, String message) {
        return replyLater(delay, quote, MiraiCode.deserializeMiraiCode(ArgumentUtility.replaceArguments(message, getXiaomingBot().getLanguage().getValues(), getXiaomingBot().getConfiguration().getMaxIterateTime())));
    }

    default ScheduableTask<M> replyLater(long delay, Message quote, M message) {
        return replyLater(delay, quote, message.getMessageChain());
    }

    List<M> getRecentMessages();

    default M nextMessage(long timeout) {
        return InteractorUtils.waitLastElement(getRecentMessages(), timeout);
    }

    default M nextMessage() {
        return nextMessage(getXiaomingBot().getConfiguration().getMaxUserInputWaitTime());
    }

    default Image uploadImage(ExternalResource resource) {
        return getMiraiContact().uploadImage(resource);
    }
}
