package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.SerializableMessage;
import cn.codethink.xiaoming.message.SummarizableMessage;

import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.compound.CompoundMessage
 * @see cn.codethink.xiaoming.message.Message
 */
public abstract class AbstractCompoundMessage
    implements CompoundMessage {
    
    private String messageCode;
    
    private String summary;
    
    @Override
    public String serializeToMessageCode() {
        if (Objects.isNull(messageCode)) {
            messageCode = Collections.toString(this, SerializableMessage::serializeToMessageCode, "");
        }
        return messageCode;
    }
    
    @Override
    public String serializeToSummary() {
        if (Objects.isNull(summary)) {
            summary = Collections.toString(this, SummarizableMessage::serializeToSummary, "");
        }
        return summary;
    }
}
