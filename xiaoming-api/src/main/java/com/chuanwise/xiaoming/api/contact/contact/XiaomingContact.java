package com.chuanwise.xiaoming.api.contact.contact;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.util.InteractorUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

public interface XiaomingContact extends XiaomingObject {
    String getCompleteName();

    Contact getMiraiContact();

    default long getCode() {
        return getMiraiContact().getId();
    }

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getName();

    String getAlias();

    String getAvatarUrl();

    default void send(String message) {
        send(MiraiCode.deserializeMiraiCode(message));
    }

    void send(MessageChain messages);

    default void send(Message messages) {
        getMiraiContact().sendMessage(messages.getMessageChain());
    }

    default AsyncResult<Boolean> sendLater(long timeout, String message) {
        return getXiaomingBot().getScheduler().run(() -> {
            try {
                Thread.sleep(timeout);
                send(message);
            } catch (InterruptedException ignored) {
            }
        });
    }

    default AsyncResult<Boolean> sendLater(long timeout, MessageChain message) {
        return getXiaomingBot().getScheduler().run(() -> {
            try {
                Thread.sleep(timeout);
                send(message);
            } catch (InterruptedException ignored) {
            }
        });
    }

    default AsyncResult<Boolean> sendLater(long timeout, Message message) {
        return getXiaomingBot().getScheduler().run(() -> {
            try {
                Thread.sleep(timeout);
                send(message);
            } catch (InterruptedException ignored) {
            }
        });
    }

    List<? extends Message> getRecentMessages();

    default Message nextMessage(long timeout) {
        return InteractorUtils.waitLastElement(getRecentMessages(), timeout);
    }
}
