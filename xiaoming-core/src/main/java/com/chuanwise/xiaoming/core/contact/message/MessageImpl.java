package com.chuanwise.xiaoming.core.contact.message;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;

@Getter
public abstract class MessageImpl extends XiaomingObjectImpl implements Message {
    @Setter
    MessageChain messageChain;
    final long time = System.currentTimeMillis();

    protected MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain) {
        super(xiaomingBot);
        this.messageChain = messageChain;
    }

    protected MessageImpl(XiaomingBot xiaomingBot, String message) {
        this(xiaomingBot, MiraiCode.deserializeMiraiCode(message));
    }

    @Override
    public String summary() {
        return messageChain.contentToString();
    }

    @Override
    public String toString() {
        return serialize();
    }
}