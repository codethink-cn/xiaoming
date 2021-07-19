package cn.chuanwise.xiaoming.core.contact.contact;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.core.contact.message.PrivateMessageImpl;
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
        return getXiaomingBot().getResourceManager().useResources(new PrivateMessageImpl(getXiaomingBot().getReceptionistManager().getBotReceptionist().forPrivate(),
                miraiContact.sendMessage(messages).getSource().getOriginalMessage()));
    }
}