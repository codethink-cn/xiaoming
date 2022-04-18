package cn.codethink.xiaoming.message.compound;

import cn.chuanwise.common.util.Collections;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.Serializable;
import cn.codethink.xiaoming.message.Summarizable;
import cn.codethink.xiaoming.property.Property;

import java.util.Map;

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
    public String serializeToMessageCode(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");

        return Collections.toString(this, x -> x.serializeToMessageCode(properties), "");
    }
    
    @Override
    public String serializeToMessageSummary(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");

        return Collections.toString(this, x -> x.serializeToMessageSummary(properties), "");
    }
}
