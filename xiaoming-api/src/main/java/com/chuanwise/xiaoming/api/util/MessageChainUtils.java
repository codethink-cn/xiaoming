package com.chuanwise.xiaoming.api.util;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;

public class MessageChainUtils extends StaticUtils {
    public static MessageChain asMessageChain(SingleMessage... messages) {
        MessageChainBuilder chainBuilder = new MessageChainBuilder(messages.length);
        for (SingleMessage message : messages) {
            chainBuilder.add(message);
        }
        return chainBuilder.asMessageChain();
    }

    public static MessageChain asMessageChain(String string) {
        return asMessageChain(new PlainText(string));
    }
}
