package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StaticUtil;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;

import java.util.ArrayList;
import java.util.List;

public class MiraiCodeUtil extends StaticUtil {
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
        return (List) CollectionUtil.filter(MiraiCode.deserializeMiraiCode(string), new ArrayList<>(), singleMessage -> singleMessage instanceof Image);
    }
}
