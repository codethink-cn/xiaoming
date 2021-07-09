package com.chuanwise.xiaoming.core.contact.contact;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import com.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import com.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

@Getter
public class PrivateContactImpl extends XiaomingContactImpl<PrivateMessage, Friend> implements PrivateContact {
    final Friend miraiContact;
    final List<PrivateMessage> recentMessages;

    public PrivateContactImpl(XiaomingBot xiaomingBot, Friend miraiContact) {
        super(xiaomingBot);
        this.miraiContact = miraiContact;
        this.recentMessages = xiaomingBot.getContactManager().forPrivateMessages(getCodeString());
    }

    @Override
    public PrivateMessage send(MessageChain messages) {
        return new PrivateMessageImpl(getXiaomingBot().getReceptionistManager().getBotReceptionist().forPrivate(), miraiContact.sendMessage(messages).getSource().getOriginalMessage());
    }
}