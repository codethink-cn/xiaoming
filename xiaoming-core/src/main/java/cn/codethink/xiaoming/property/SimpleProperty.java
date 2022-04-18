package cn.codethink.xiaoming.property;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;

import java.util.Map;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.property.Property
 */
@SuppressWarnings("all")
public class SimpleProperty<T>
    implements Property<T> {
    
    @Override
    public T get(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");
        return (T) properties.get(this);
    }
    
    @Override
    public T get(PropertyHolder propertyHolder) {
        Preconditions.objectNonNull(propertyHolder, "property holder");
        return (T) propertyHolder.getProperties().get(this);
    }
    
    @Override
    public T getOrFail(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");
        return (T) Maps.getOrFail(properties, this);
    }
    
    @Override
    public T getOrFail(PropertyHolder propertyHolder) {
        Preconditions.objectNonNull(propertyHolder, "property holder");
        return (T) Maps.getOrFail(propertyHolder.getProperties(), this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
