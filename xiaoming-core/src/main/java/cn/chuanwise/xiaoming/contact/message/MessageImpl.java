package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.XiaomingObjectImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

@EqualsAndHashCode
public class MessageImpl extends XiaomingObjectImpl implements Message {
    @Setter
    @Getter
    MessageChain messageChain;
    String serializedMessageChain;

    @Setter
    @Getter
    MessageChain originalMessageChain;

    @Getter
    final long time;

    @Override
    public void setMessageChain(MessageChain messageChain) {
        this.messageChain = messageChain;
        serializedMessageChain = messageChain.serializeToMiraiCode();
    }

    @Override
    public String serialize() {
        return serializedMessageChain;
    }

    public MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain) {
        this(xiaomingBot, messageChain, System.currentTimeMillis());
    }

    public MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain, long time) {
        this(xiaomingBot, messageChain, messageChain, time);
    }

    public MessageImpl(XiaomingBot xiaomingBot, String message, long time) {
        this(xiaomingBot, MiraiCode.deserializeMiraiCode(message), time);
    }

    public MessageImpl(XiaomingBot xiaomingBot, String message) {
        this(xiaomingBot, message, System.currentTimeMillis());
    }

    public MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain, MessageChain originalMessageChain, long time) {
        setXiaomingBot(xiaomingBot);
        this.messageChain = messageChain;
        this.originalMessageChain = originalMessageChain;
        serializedMessageChain = messageChain.serializeToMiraiCode();
        this.time = time;
    }

    @Override
    public String toString() {
        return serializedMessageChain;
    }
}