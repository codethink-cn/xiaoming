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
    String serializedOriginalMessageChain;

    @Getter
    final int[] internalMessageCode, messageCode;

    @Getter
    final long time;

    @Override
    public void setMessageChain(MessageChain messageChain) {
        this.messageChain = messageChain;
        serializedMessageChain = messageChain.serializeToMiraiCode();
    }

    @Override
    public void setOriginalMessageChain(MessageChain originalMessageChain) {
        this.originalMessageChain = originalMessageChain;
        serializedOriginalMessageChain = originalMessageChain.serializeToMiraiCode();
    }

    @Override
    public String serialize() {
        return serializedMessageChain;
    }

    @Override
    public String serializeOriginalMessage() {
        return serializedOriginalMessageChain;
    }

    public MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain) {
        this(xiaomingBot, messageChain, System.currentTimeMillis());
    }

    public MessageImpl(XiaomingBot xiaomingBot, MessageChain messageChain, long time) {
        this(xiaomingBot, messageChain, null, null, time);
    }

    public MessageImpl(XiaomingBot xiaomingBot,
                       MessageChain messageChain,
                       int[] messageCode,
                       int[] internalMessageCode,
                       long time) {
        setXiaomingBot(xiaomingBot);
        setMessageChain(messageChain);
        setOriginalMessageChain(messageChain);
        this.time = time;

        this.messageCode = messageCode;
        this.internalMessageCode = internalMessageCode;
    }

    @Override
    public String toString() {
        return serializedMessageChain;
    }
}