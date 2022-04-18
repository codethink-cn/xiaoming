package cn.codethink.xiaoming.property;

import cn.chuanwise.common.util.Maps;
import cn.chuanwise.common.util.Preconditions;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Chuanwise
 *
 * @see Property
 */
@SuppressWarnings("all")
public class CustomGetterProperty<T>
    implements Property<T> {
    
    private final Function<Map<Property<?>, Object>, T> getter;
    
    public CustomGetterProperty(Function<Map<Property<?>, Object>, T> getter) {
        Preconditions.objectNonNull(getter, "getter");
        
        this.getter = getter;
    }
    
    @Override
    public T get(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");
        
        T value = (T) properties.get(this);
        if (Objects.isNull(value)) {
            value = (T) getter.apply(properties);
        }
    
        return value;
    }
    
    @Override
    public T get(PropertyHolder propertyHolder) {
        Preconditions.objectNonNull(propertyHolder, "property holder");
        final Map<Property<?>, Object> properties = propertyHolder.getProperties();
    
        T value = (T) properties.get(this);
        if (Objects.isNull(value)) {
            value = (T) getter.apply(properties);
        }
    
        return value;
    }
    
    @Override
    public T getOrFail(Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(properties, "properties");
        
        T value = (T) properties.get(this);
        if (Objects.isNull(value)) {
            value = (T) getter.apply(properties);
        }
        
        Preconditions.elementNonNull(value);
        return value;
    }
    
    @Override
    public T getOrFail(PropertyHolder propertyHolder) {
        Preconditions.objectNonNull(propertyHolder, "property holder");
        final Map<Property<?>, Object> properties = propertyHolder.getProperties();
    
        T value = (T) properties.get(this);
        if (Objects.isNull(value)) {
            value = (T) getter.apply(properties);
        }
    
        Preconditions.elementNonNull(value);
        return value;
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
