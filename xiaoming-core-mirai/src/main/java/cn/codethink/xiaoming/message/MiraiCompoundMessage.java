package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.message.compound.ListCompoundMessage;
import cn.codethink.xiaoming.message.element.BasicMessage;
import cn.codethink.xiaoming.message.element.MessageMetadataType;
import lombok.Data;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Map;

/**
 * Mirai 平台下的消息。
 * 此消息只是为了方便缓存一个消息链 {@link MessageChain}，该消息链可以是在线的，也可以是离线的。
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class MiraiCompoundMessage
    extends ListCompoundMessage {
    
    // TODO: 2022/4/16 confirm if this class can be saved
    private final MessageChain messageChain = null;

    public MiraiCompoundMessage(List<BasicMessage> basicMessages, Map<MessageMetadataType<?>, Object> metadata) {
        super(basicMessages, metadata);
        
        
    }
    
    public String serializeToMiraiCode() {
        return messageChain.serializeToMiraiCode();
    }
}
