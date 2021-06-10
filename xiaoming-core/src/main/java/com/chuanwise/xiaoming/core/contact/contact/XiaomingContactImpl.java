package com.chuanwise.xiaoming.core.contact.contact;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class XiaomingContactImpl extends XiaomingObjectImpl implements XiaomingContact {
    public XiaomingContactImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    public void send(MessageChain messages) {
        getMiraiContact().sendMessage(getXiaomingBot().getResourceManager().useResources(messages, getMiraiContact()));
    }
}
