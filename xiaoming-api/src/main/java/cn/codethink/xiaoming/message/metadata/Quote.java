package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MessageCodeBuilder;
import cn.codethink.xiaoming.message.basic.MessageMetadataType;
import cn.codethink.xiaoming.message.basic.MessageMetadata;
import lombok.Data;

/**
 * 引用某条消息
 *
 * @author Chuanwise
 */
@Data
public class Quote
    implements MessageMetadata {
    
    private final MessageReference messageReference;
    
    @Override
    public String serializeToMessageCode() {
        return new MessageCodeBuilder("quote")
            .argument(messageReference.serializeToMessageCode())
            .build();
    }
    
    @Override
    public MessageMetadataType<Quote> getMetadataType() {
        return MessageMetadataType.QUOTE;
    }
}
