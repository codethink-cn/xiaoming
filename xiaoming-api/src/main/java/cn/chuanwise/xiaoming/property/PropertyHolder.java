package cn.chuanwise.xiaoming.property;

import cn.chuanwise.toolkit.enumerate.WeekReason;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.utility.ObjectUtility;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface PropertyHolder {
    long WAIT_PROPERTY_TIMEOUT = TimeUnit.MINUTES.toMillis(5);

    Map<PropertyType, Object> getPropertyConditionalVariables();

    Map<PropertyType, Object> getProperties();

    default <T> void setProperty(PropertyType<T> type, T value) {
        final Map<PropertyType, Object> properties = getProperties();
        final Map<PropertyType, Object> propertyWaiters = getPropertyConditionalVariables();

        synchronized (properties) {
            synchronized (propertyWaiters) {
                properties.put(type, value);

                // 唤醒那些正在等待的线程
                final Object conditionalVariable = propertyWaiters.get(type);
                if (Objects.nonNull(conditionalVariable)) {
                    synchronized (conditionalVariable) {
                        conditionalVariable.notifyAll();
                    }
                    propertyWaiters.remove(type);
                }
            }
        }
    }

    default <T> T getProperty(PropertyType<T> type) {
        return (T) getProperties().get(type);
    }

    default <T> T getPropertyOrDefault(PropertyType<T> type, T defaultValue) {
        final T value = getProperty(type);
        return ObjectUtility.firstNonNull(value, defaultValue);
    }

    default <T> T removeProperty(PropertyType<T> type) {
        return (T) getProperties().remove(type);
    }

    default void clearProperties() {
        getProperties().clear();
    }

    default boolean hasProperty(PropertyType<?> type) {
        return getProperties().containsKey(type);
    }

    /**
     * 在一个属性上等待，直到有人把这个属性填充了
     * @param type 属性类型
     * @param timeout 最长等待时间
     * @return 等待的结果，或者为 {@code null}
     */
    default <T> T waitPropertyOrSupply(PropertyType<T> type, Supplier<T> defaultValueSupplier, long timeout) {
        // 获得在当前属性上等待的那些线程们
        final Object conditionalVariable = MapUtility.getOrPutSupply(getPropertyConditionalVariables(), type, Object::new);

        if (ObjectUtility.wait(conditionalVariable, timeout) == WeekReason.NOTIFY) {
            return getProperty(type);
        } else {
            return defaultValueSupplier.get();
        }
    }

    default <T> T waitPropertyOrDefault(PropertyType<T> type, T defaultValue, long timeout) {
        return waitPropertyOrSupply(type, () -> defaultValue, timeout);
    }

    default <T> T waitProperty(PropertyType<T> type) {
        return waitPropertyOrDefault(type, null, WAIT_PROPERTY_TIMEOUT);
    }

    default <T> T waitProperty(PropertyType<T> type, long timeout) {
        return waitPropertyOrDefault(type, null, timeout);
    }

    default <T> T waitPropertyOrSupply(PropertyType<T> type, Supplier<T> defaultValueSupplier) {
        return waitPropertyOrSupply(type, defaultValueSupplier, WAIT_PROPERTY_TIMEOUT);
    }

    default <T> T waitPropertyOrDefault(PropertyType<T> type, T defaultValue) {
        return waitPropertyOrDefault(type, defaultValue, WAIT_PROPERTY_TIMEOUT);
    }
}
