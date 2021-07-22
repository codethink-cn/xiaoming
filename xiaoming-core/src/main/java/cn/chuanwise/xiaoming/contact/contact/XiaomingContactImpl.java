package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.XiaomingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;

@Getter
public abstract class XiaomingContactImpl<M extends Message, MC extends Contact> extends XiaomingObjectImpl implements XiaomingContact<M, MC> {
    public XiaomingContactImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }
}
