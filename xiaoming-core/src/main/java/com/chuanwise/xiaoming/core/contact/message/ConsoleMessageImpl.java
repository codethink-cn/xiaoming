package com.chuanwise.xiaoming.core.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.ConsoleContact;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public class ConsoleMessageImpl extends MessageImpl implements ConsoleMessage {
    final ConsoleXiaomingUser sender;
    final ConsoleContact contact;

    public ConsoleMessageImpl(ConsoleXiaomingUser sender, MessageChain messages) {
        super(sender.getXiaomingBot(), messages);
        this.sender = sender;
        this.contact = sender.getContact();
    }

    @Override
    public ConsoleMessage clone() throws CloneNotSupportedException {
        return ((ConsoleMessage) super.clone());
    }
}