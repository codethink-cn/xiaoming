package cn.codethink.xiaoming.property;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.property.PropertyHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chuanwise
 *
 * @see PropertyHolder
 */
public class PropertyHolderImpl
    implements PropertyHolder {
    
    private final Map<Property<?>, Object> properties;
    
    public PropertyHolderImpl() {
        this.properties = new HashMap<>();
    }
    
    public PropertyHolderImpl(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");
        
        this.properties = properties;
    }
    
    @Override
    public Map<Property<?>, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(Property<T> property) {
        return (T) properties.get(property);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPropertyOrFail(Property<T> property) {
        return (T) Maps.getOrFail(properties, property);
    }
    
    public <T> T setProperty(Property<T> property, T value) {
        return (T) properties.put(property, value);
    }
}
