package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.message.content.MessageContent;
import cn.codethink.xiaoming.message.content.MiraiMessageContentSerializer;
import cn.codethink.common.util.StaticUtilities;
import net.mamoe.mirai.message.data.MessageChain;

/**
 * @author Chuanwise
 */
public class MiraiMessageChain
        extends StaticUtilities {
    
    /**
     * 将 Mirai 消息链转化为消息内容
     *
     * @param messageChain Mirai 消息链
     * @return 消息内容
     */
    public static MessageContent deserialize(MessageChain messageChain) {
        return MiraiMessageContentSerializer.getInstance().deserialize(messageChain);
    }
    
    /**
     * 将消息内容转化为 Mirai 消息链
     *
     * @param messageContent 消息内容
     * @return Mirai 消息链
     */
    public static MessageChain serialize(MessageContent messageContent) {
        return MiraiMessageContentSerializer.getInstance().serialize(messageContent);
    }
}