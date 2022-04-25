package cn.codethink.xiaoming.message;

import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.compound.ListCompoundMessage;
import cn.codethink.xiaoming.message.basic.BasicMessage;
import cn.codethink.xiaoming.property.Property;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Map;

/**
 * Qq 平台下的消息。
 * 此消息只是为了方便缓存一个消息链 {@link MessageChain}，该消息链可以是在线的，也可以是离线的。
 *
 * @author Chuanwise
 */
@SuppressWarnings("all")
public class QqCompoundMessage
    extends ListCompoundMessage {
    
    // TODO: 2022/4/16 confirm if this class can be saved
    private final MessageChain messageChain = null;

    public QqCompoundMessage(List<BasicMessage> basicMessages, Map<Property<?>, MessageMetadata> metadata) {
        super(basicMessages, metadata);
        
        
    }
    
    public String serializeToMiraiCode() {
        return messageChain.serializeToMiraiCode();
    }
}
