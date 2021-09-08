package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.object.XiaomingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.OnlineMessageSource;

@Getter
public abstract class XiaomingContactImpl<C extends Contact> extends XiaomingObjectImpl implements XiaomingContact<C> {
    final C miraiContact;

    public XiaomingContactImpl(XiaomingBot xiaomingBot, C miraiContact) {
        super(xiaomingBot);
        this.miraiContact = miraiContact;
    }

    @Override
    public Message sendMessage(MessageChain messages) {
        final MessageReceipt messageReceipt = miraiContact.sendMessage(messages);
        final OnlineMessageSource.Outgoing source = messageReceipt.getSource();
        return new MessageImpl(xiaomingBot, source.getOriginalMessage(), source.getTime());
    }
}
