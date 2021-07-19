package cn.chuanwise.xiaoming.core.contact.contact;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;

@Getter
public abstract class XiaomingContactImpl<M extends Message, MC extends Contact> extends XiaomingObjectImpl implements XiaomingContact<M, MC> {
    public XiaomingContactImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }
}
