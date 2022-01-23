package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.object.XiaomingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Optional;

@Getter
public abstract class XiaomingContactImpl<C extends Contact> extends XiaomingObjectImpl implements XiaomingContact<C> {
    final C miraiContact;

    public XiaomingContactImpl(XiaomingBot xiaomingBot, C miraiContact) {
        super(xiaomingBot);
        this.miraiContact = miraiContact;
    }

    @Override
    public Optional<Message> sendMessage(MessageChain messages) {
        final SendMessageEvent event = new SendMessageEvent(this, messages);

        xiaomingBot.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            return Optional.empty();
        } else {
            return event.getMessageBox().toOptional();
        }
    }
}
