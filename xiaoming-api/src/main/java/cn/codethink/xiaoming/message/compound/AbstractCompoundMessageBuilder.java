package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.Summarizable;

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
        return Collections.toString(this, Serializable::serializeToMessageCode, "");
    }
    
    @Override
    public String serializeToSummary() {
        return Collections.toString(this, Summarizable::serializeToSummary, "");
    }
}
