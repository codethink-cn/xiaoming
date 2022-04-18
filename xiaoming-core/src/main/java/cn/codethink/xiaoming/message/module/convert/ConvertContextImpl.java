package cn.codethink.xiaoming.message.module.convert;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.property.PropertyHolderImpl;
import lombok.Data;

import java.util.Map;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.module.convert.ConvertContext
 */
@Data
public class ConvertContextImpl
    extends PropertyHolderImpl
    implements ConvertContext {

    private final Object source;

    private final Class<?> targetClass;
    
    public ConvertContextImpl(Object source, Class<?> targetClass, Map<Property<?>, Object> properties) {
        super(properties);
        
        Preconditions.objectNonNull(source, "source");
        Preconditions.objectNonNull(targetClass, "target class");
        
        this.targetClass = targetClass;
        this.source = source;
    }
    
    @Override
    public Bot getBot() {
        return getProperty(Property.BOT);
    }
}
