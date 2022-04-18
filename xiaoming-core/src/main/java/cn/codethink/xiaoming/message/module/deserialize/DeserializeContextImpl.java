package cn.codethink.xiaoming.message.module.deserialize;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.property.PropertyHolderImpl;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.module.deserialize.DeserializeContext
 */
@Data
public class DeserializeContextImpl
    extends PropertyHolderImpl
    implements DeserializeContext {
    
    private final List<String> arguments;
    
    public DeserializeContextImpl(List<String> arguments, Map<Property<?>, Object> properties) {
        super(properties);
    
        Preconditions.objectArgumentNonEmpty(arguments, "arguments");
        
        this.arguments = arguments;
    }
    
    @Override
    public Bot getBot() {
        return getProperty(Property.BOT);
    }
}
