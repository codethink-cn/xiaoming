package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.api.recept.ConsoleReceptionTask;
import com.chuanwise.xiaoming.api.recept.Receptionist;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.api.util.ArgumentUtils;
import com.chuanwise.xiaoming.core.contact.message.ConsoleMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import com.chuanwise.xiaoming.core.recept.ConsoleReceptionTaskImpl;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@Data
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl<ConsoleContact, ConsoleMessage, ConsoleReceptionTask> implements ConsoleXiaomingUser {
    long code;
    final ConsoleContact contact;
    final List<ConsoleMessage> recentMessages = new LinkedList<>();
    ConsoleReceptionTask receptionTask;

    public ConsoleXiaomingUserImpl(ConsoleContact contact) {
        super(contact.getXiaomingBot(), 0);
        this.contact = contact;
    }

    public void setCode(long code) {
        this.code = code;
    }

    @Override
    public String getCompleteName() {
        return "后台";
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new ConsoleMessageImpl(this, messages));
    }

    @Override
    public void onNextInput(ConsoleMessage message) {
        final List<ConsoleMessage> list = getRecentMessages();
        setProperty("last", message.serialize());
        list.add(message);

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);

        synchronized (list) {
            list.notifyAll();
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public ConsoleMessage sendPrivateMessage(String message, Object... arguments) {
        sendMessage(message, arguments);
        final MessageChain messages = MiraiCode.deserializeMiraiCode(replaceArguments(message, arguments));
        return new ConsoleMessageImpl(this, messages);
    }
}
