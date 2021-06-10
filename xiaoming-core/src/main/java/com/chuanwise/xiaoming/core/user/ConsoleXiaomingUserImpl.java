package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.recept.ConsoleReceptionTask;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.core.contact.message.ConsoleMessageImpl;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import lombok.Data;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Chuanwise
 */
@Data
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl implements ConsoleXiaomingUser {
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
}
