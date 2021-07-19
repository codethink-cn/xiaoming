package cn.chuanwise.xiaoming.core.contact.contact;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.core.contact.message.GroupMessageImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

@Getter
public class GroupContactImpl extends XiaomingContactImpl<GroupMessage, Group> implements GroupContact {
    final Group miraiContact;
    final List<GroupMessage> recentMessages;

    public GroupContactImpl(XiaomingBot xiaomingBot, Group miraiContact) {
        super(xiaomingBot);
        this.miraiContact = miraiContact;
        this.recentMessages = getXiaomingBot().getContactManager().forGroupMessages(getCodeString());
    }

    @Override
    public GroupMessage send(MessageChain messages) {
        return new GroupMessageImpl(getXiaomingBot().getReceptionistManager().getBotReceptionist().forGroup(getCode()),
                miraiContact.sendMessage(messages).getSource().getOriginalMessage());
    }
}