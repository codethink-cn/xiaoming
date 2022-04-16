package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.SerializableMessage;
import cn.codethink.xiaoming.message.SummarizableMessage;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.compound.CompoundMessageBuilder
 * @see cn.codethink.xiaoming.message.compound.CompoundMessage
 * @see cn.codethink.xiaoming.message.Message
 */
public abstract class AbstractCompoundMessageBuilder
    implements CompoundMessageBuilder {
    
    @Override
    public String serializeToMessageCode() {
        return Collections.toString(this, SerializableMessage::serializeToMessageCode, "");
    }
    
    @Override
    public String serializeToSummary() {
        return Collections.toString(this, SummarizableMessage::serializeToSummary, "");
    }
}
