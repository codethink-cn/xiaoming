package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.recept.PrivateReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public class PrivateXiaomingUserImpl extends XiaomingUserImpl<PrivateContact, PrivateMessage, PrivateReceptionTask> implements PrivateXiaomingUser {
    final PrivateContact contact;
    final List<PrivateMessage> recentMessages;

    @Setter
    PrivateReceptionTask receptionTask;

    public PrivateXiaomingUserImpl(PrivateContact contact, List<PrivateMessage> recentMessages) {
        super(contact.getXiaomingBot(), contact.getCode());
        this.contact = contact;
        this.recentMessages = recentMessages;
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new PrivateMessageImpl(this, messages));
    }

    @Override
    public void onNextInput(PrivateMessage message) {
        final List<PrivateMessage> list = getRecentMessages();
        setProperty("last", message.serialize());
        list.add(message);

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);

        final PrivateReceptionTask receptionTask = getReceptionTask();
        if (Objects.isNull(receptionTask)) {
            receptionist.onPrivateMessage(getContact(), message);
        }

        synchronized (list) {
            list.notifyAll();
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        final String replacedMessage = replaceArguments(message, arguments);
        if (isUsingBuffer()) {
            appendBuffer(replacedMessage);
        } else {
            contact.send(replacedMessage);
        }
    }

    @Override
    public void sendPrivateMessage(String message, Object... arguments) {
        sendMessage(message, arguments);
    }

    @Override
    public long getCode() {
        return contact.getCode();
    }

    @Override
    public String getName() {
        return contact.getName();
    }

    @Override
    public String getCompleteName() {
        return contact.getCompleteName();
    }
}
