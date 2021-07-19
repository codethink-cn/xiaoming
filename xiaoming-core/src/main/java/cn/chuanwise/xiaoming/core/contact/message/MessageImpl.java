package cn.chuanwise.xiaoming.core.contact.message;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.contact.message.Message;
import cn.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public abstract class MessageImpl extends XiaomingObjectImpl implements Message {
    @Setter
    MessageChain messageChain;
    String serializedMessageChain;

    @Setter
    MessageChain originalMessageChain;
    final long time = System.currentTimeMillis();

    public void setMessageChain(MessageChain messageChain) {
        this.messageChain = messageChain;
        serializedMessageChain = messageChain.serializeToMiraiCode();
    }

    @Override
    public String serialize() {
        return serializedMessageChain;
    }

    protected MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain) {
        super(xiaomingBot);
        this.originalMessageChain = messageChain;
        setMessageChain(messageChain);
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

    @Override
    public Message clone() throws CloneNotSupportedException {
        return ((Message) super.clone());
    }
}