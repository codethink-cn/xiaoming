package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.reference.MessageReference;
import lombok.Data;

/**
 * 引用某条消息
 *
 * @author Chuanwise
 */
@Data
public class Quote
    implements MetadataMessage {
    
    private final MessageReference messageReference;
    
    @Override
    public String serializeToMessageCode() {
        return MessageCode.builder("quote")
            .argument(messageReference.serializeToMessageCode())
            .build();
    }
    
    @Override
    public MessageMetadataType<Quote> getMetadataType() {
        return MessageMetadataType.QUOTE;
    }
}
