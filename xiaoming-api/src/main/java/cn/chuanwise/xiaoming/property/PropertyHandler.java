package cn.chuanwise.xiaoming.property;

import cn.chuanwise.toolkit.container.Container;

import java.util.Map;
import java.util.Objects;

public interface PropertyHandler {
    Map<PropertyType, Object> getProperties();

    <T> void setProperty(PropertyType<T> type, T value);

    default <T> Container<T> getProperty(PropertyType<T> type) {
        if (Objects.isNull(type)) {
            return Container.empty();
        }
        final Map<PropertyType, Object> properties = getProperties();
        if (properties.containsKey(type)) {
            return Container.of((T) properties.get(type));
        } else {
            return Container.empty();
        }
    }

    <T> Container<T> removeProperty(PropertyType<T> type);

    default void clearProperties() {
        getProperties().clear();
    }

    default boolean hasProperty(PropertyType<?> type) {
        return getProperties().containsKey(type);
    }

    <T> Container<T> waitProperty(PropertyType<T> type, long timeout) throws InterruptedException;
}
