package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.account.record.PrivateCommandRecord;
import cn.chuanwise.xiaoming.client.CenterClientManager;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.recept.PrivateReceptionTask;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.contact.message.PrivateMessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Objects;

@Getter
public class PrivateXiaomingUserImpl extends XiaomingUserImpl<PrivateContact, PrivateMessage, PrivateReceptionTask> implements PrivateXiaomingUser {
    final PrivateContact contact;
    final List<PrivateMessage> recentMessages;

    @Setter
    PrivateReceptionTask receptionTask;

    public PrivateXiaomingUserImpl(PrivateContact contact) {
        super(contact.getXiaomingBot(), contact.getCode());
        this.contact = contact;
        this.recentMessages = contact.getRecentMessages();
    }

    @Override
    public PrivateMessage buildMessage(MessageChain messages) {
        return new PrivateMessageImpl(this, messages);
    }

    @Override
    public void onNextInput(PrivateMessage message) {
        final List<PrivateMessage> list = getRecentMessages();
//        setProperty("last", message.serialize());

        final PrivateReceptionTask receptionTask = getReceptionTask();
        if (Objects.isNull(receptionTask)) {
            receptionist.onPrivateMessage(getContact(), message);
            return;
        }

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);
        synchronized (receptionist) {
            receptionist.notifyAll();
        }
        synchronized (list) {
            list.add(message);
            list.notifyAll();
        }
    }

    @Override
    public void sendMessage(String message, Object... contexts) {
        final String replacedMessage = format(message, contexts);
        if (isUsingBuffer()) {
            appendBuffer(replacedMessage);
        } else {
            contact.send(replacedMessage);
        }
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
