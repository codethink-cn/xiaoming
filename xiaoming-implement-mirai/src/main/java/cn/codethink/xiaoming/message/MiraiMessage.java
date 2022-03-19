package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.content.MessageContent;
import cn.codethink.common.util.Preconditions;
import lombok.Data;
import net.mamoe.mirai.message.data.MessageChain;

/**
 * Mirai 平台下的消息
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class MiraiMessage
    extends AbstractMessage {
    
    private final MessageChain messageChain;
    
    public MiraiMessage(Code code, MessageContent messageContent, long timeMillis, MessageChain messageChain) {
        super(code, messageContent, timeMillis);
    
        Preconditions.namedArgumentNonNull(messageChain, "message chain");
        
        this.messageChain = messageChain;
    }
}
