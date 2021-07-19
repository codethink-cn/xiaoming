package cn.chuanwise.xiaoming.api.utility;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.StaticUtility;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MiraiCodeUtility extends StaticUtility {
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

    public static String contentToString(String string) {
        return MiraiCode.deserializeMiraiCode(string).contentToString();
    }

    public static List<Image> getImages(String string) {
        return (List) CollectionUtility.filter(MiraiCode.deserializeMiraiCode(string), new ArrayList<>(), singleMessage -> singleMessage instanceof Image);
    }
}
