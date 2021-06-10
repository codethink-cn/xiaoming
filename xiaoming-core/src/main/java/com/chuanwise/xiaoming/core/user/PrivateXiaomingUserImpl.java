package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.recept.PrivateReceptionTask;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;

@Getter
public class PrivateXiaomingUserImpl extends XiaomingUserImpl implements PrivateXiaomingUser {
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
    public void onNextInput(Message message) {
        if (message instanceof PrivateMessage) {
            onNextInput(((PrivateMessage) message));
        } else {
            throw new XiaomingRuntimeException("消息类型错误");
        }
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new PrivateMessageImpl(this, messages));
    }

    @Override
    public void sendMessage(String message, Object... arguments) {
        contact.send(replaceArguments(message, arguments));
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
