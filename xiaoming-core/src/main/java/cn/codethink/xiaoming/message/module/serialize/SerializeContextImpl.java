package cn.codethink.xiaoming.message.module.serialize;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.property.PropertyHolderImpl;
import lombok.Data;

import java.util.Map;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.module.serialize.SerializeContext
 */
@Data
public class SerializeContextImpl
    extends PropertyHolderImpl
    implements SerializeContext {
    
    private final Object source;
    
    public SerializeContextImpl(Object source, Map<Property<?>, Object> properties) {
        super(properties);
    
        Preconditions.objectNonNull(source, "source");
        
        this.source = source;
    }
    
    @Override
    public Bot getBot() {
        return getProperty(Property.BOT);
    }
}
