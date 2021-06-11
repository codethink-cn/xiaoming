package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.recept.TempReceptionTask;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.TempMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;

@Getter
public class TempXiaomingUserImpl extends XiaomingUserImpl<TempContact, TempMessage, TempReceptionTask> implements TempXiaomingUser {
    final TempContact contact;
    final List<TempMessage> recentMessages;

    @Setter
    TempReceptionTask receptionTask;

    public TempXiaomingUserImpl(TempContact tempContact, List<TempMessage> recentMessages) {
        super(tempContact.getXiaomingBot(), tempContact.getCode());
        this.contact = tempContact;
        this.recentMessages = recentMessages;
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new TempMessageImpl(this, messages));
    }

    @Override
    public long getCode() {
        return contact.getCode();
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
    public String getName() {
        return contact.getName();
    }

    @Override
    public String getCompleteName() {
        return contact.getCompleteName();
    }
}
