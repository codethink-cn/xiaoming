package com.chuanwise.xiaoming.core.contact.contact;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.response.ResponseGroup;
import com.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public class GroupContactImpl extends XiaomingContactImpl<GroupMessage, Group> implements GroupContact {
    final Group miraiContact;
    final List<GroupMessage> recentMessages;

    public GroupContactImpl(XiaomingBot xiaomingBot, Group miraiContact) {
        super(xiaomingBot);
        this.miraiContact = miraiContact;
        recentMessages = getXiaomingBot().getContactManager().getOrPutGroupRecentMessages(getCodeString());
    }

    @Override
    public GroupMessage send(MessageChain messages) {
        return new GroupMessageImpl(getXiaomingBot().getReceptionistManager().getBotReceptionist().getOrPutGroupXiaomingUser(this,
                getMember(getXiaomingBot().getMiraiBot().getId())),
                miraiContact.sendMessage(messages).getSource().getOriginalMessage());
    }
}