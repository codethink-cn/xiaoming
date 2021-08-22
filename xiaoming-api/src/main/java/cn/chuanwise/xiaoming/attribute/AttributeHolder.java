package cn.chuanwise.xiaoming.attribute;

import cn.chuanwise.toolkit.enumerate.WeekReason;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.utility.ObjectUtility;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface AttributeHolder {
    long WAIT_PROPERTY_TIMEOUT = TimeUnit.MINUTES.toMillis(5);

    Map<AttributeType, Object> getAttributeConditionalVariables();

    Map<AttributeType, Object> getAttributes();

    default <T> void setAttribute(AttributeType<T> type, T value) {
        final Map<AttributeType, Object> properties = getAttributes();
        final Map<AttributeType, Object> propertyWaiters = getAttributeConditionalVariables();

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

    default <T> T getAttribute(AttributeType<T> type) {
        if (Objects.isNull(type)) {
            return null;
        }
        final T result = (T) getAttributes().get(type);
        return result;
    }

    default <T> T getAttributeOrPutSupply(AttributeType<T> type, Supplier<T> defaultValueConsumer) {
        T attribute = (T) getAttributes().get(type);
        if (Objects.isNull(attribute)) {
            attribute = defaultValueConsumer.get();
            setAttribute(type, attribute);
        }
        return attribute;
    }

    default <T> T getAttributeOrPutDefault(AttributeType<T> type, T defaultValue) {
        return getAttributeOrPutSupply(type, () -> defaultValue);
    }

    default <T> T getAttributeOrDefault(AttributeType<T> type, T defaultValue) {
        return getAttributeOrSupply(type, () -> defaultValue);
    }

    default <T> T getAttributeOrSupply(AttributeType<T> type, Supplier<T> defaultValueConsumer) {
        final T attribute = (T) getAttributes().get(type);
        if (Objects.isNull(attribute)) {
            return defaultValueConsumer.get();
        } else {
            return attribute;
        }
    }

    default <T> T removeProperty(AttributeType<T> type) {
        return (T) getAttributes().remove(type);
    }

    default void clearProperties() {
        getAttributes().clear();
    }

    default boolean hasProperty(AttributeType<?> type) {
        return getAttributes().containsKey(type);
    }

    /**
     * 在一个属性上等待，直到有人把这个属性填充了
     * @param type 属性类型
     * @param timeout 最长等待时间
     * @return 等待的结果，或者为 {@code null}
     */
    default <T> T waitPropertyOrSupply(AttributeType<T> type, Supplier<T> defaultValueSupplier, long timeout) {
        // 获得在当前属性上等待的那些线程们
        final Object conditionalVariable = MapUtility.getOrPutSupply(getAttributeConditionalVariables(), type, Object::new);

        if (ObjectUtility.wait(conditionalVariable, timeout) == WeekReason.NOTIFY) {
            return getAttribute(type);
        } else {
            return defaultValueSupplier.get();
        }
    }

    default <T> T waitPropertyOrDefault(AttributeType<T> type, T defaultValue, long timeout) {
        return waitPropertyOrSupply(type, () -> defaultValue, timeout);
    }

    default <T> T waitProperty(AttributeType<T> type) {
        return waitPropertyOrDefault(type, null, WAIT_PROPERTY_TIMEOUT);
    }

    default <T> T waitProperty(AttributeType<T> type, long timeout) {
        return waitPropertyOrDefault(type, null, timeout);
    }

    default <T> T waitPropertyOrSupply(AttributeType<T> type, Supplier<T> defaultValueSupplier) {
        return waitPropertyOrSupply(type, defaultValueSupplier, WAIT_PROPERTY_TIMEOUT);
    }

    default <T> T waitPropertyOrDefault(AttributeType<T> type, T defaultValue) {
        return waitPropertyOrDefault(type, defaultValue, WAIT_PROPERTY_TIMEOUT);
    }
}
