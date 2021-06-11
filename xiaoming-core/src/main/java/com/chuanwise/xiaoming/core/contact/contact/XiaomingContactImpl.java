package com.chuanwise.xiaoming.core.contact.contact;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class XiaomingContactImpl<M extends Message, MC extends Contact> extends XiaomingObjectImpl implements XiaomingContact<M, MC> {
    public XiaomingContactImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }
}
