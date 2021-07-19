package cn.chuanwise.xiaoming.api.object;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface PropertyHolder {
    long TIMEOUT_TIME = TimeUnit.MINUTES.toMillis(1);

    // 可能有很多个接待者都在等待某个属性
    Map<String, Set<Thread>> getPropertyWaiters();

    Map<String, Object> getProperties();

    default void setProperty(String name, Object content) {
        final Map<String, Object> properties = getProperties();
        final Map<String, Set<Thread>> propertyWaiters = getPropertyWaiters();

        synchronized (properties) {
            synchronized (propertyWaiters) {
                properties.put(name, content);

                // 唤醒那些正在等待的线程
                final Set<Thread> threads = propertyWaiters.get(name);
                if (Objects.nonNull(threads)) {
                    synchronized (threads) {
                        threads.notifyAll();
                    }
                    propertyWaiters.remove(name);
                }
            }
        }
    }

    default Object getProperty(String name) {
        final Map<String, Object> properties = getProperties();
        synchronized (properties) {
            return properties.get(name);
        }
    }

    default <T> T getProperty(String name, Class<T> clazz) {
        return ((T) getProperty(name));
    }

    default <T> T getPropertyOrDefault(String name, T defaultContent) {
        final Object property = getProperty(name);
        return Objects.isNull(property) ? defaultContent : (((T) property));
    }

    default void removeProperty(String name) {
        final Map<String, Object> properties = getProperties();
        synchronized (properties) {
            properties.remove(name);
        }
    }

    default void clearProperties() {
        final Map<String, Object> properties = getProperties();
        synchronized (properties) {
            properties.clear();
        }
    }

    default boolean hasProperty(String name) {
        final Map<String, Object> properties = getProperties();
        synchronized (properties) {
            return properties.containsKey(name);
        }
    }

    /**
     * 在一个属性上等待，直到有人把这个属性填充了
     * @param name 属性名
     * @param timeout 最长等待时间
     * @return 等待的结果，或者为 {@code null}
     */
    default Object waitProperty(String name, long timeout) {
        // 如果之前有这个属性就丢掉
        if (hasProperty(name)) {
            removeProperty(name);
        }

        // 获得在当前属性上等待的那些线程们
        Set<Thread> threads = getPropertyWaiters().get(name);
        if (Objects.isNull(threads)) {
            threads = new HashSet<>();
            getPropertyWaiters().put(name, threads);
        }

        // 把当前线程加进去
        threads.add(Thread.currentThread());

        try {
            synchronized (threads) {
                threads.wait(timeout);
            }
        } catch (InterruptedException ignored) {
        }
        return getProperty(name);
    }

    default Object waitProperty(String name) {
        return waitProperty(name, TIMEOUT_TIME);
    }

    default <T> T waitProperty(String name, T defaultValue) {
        final Object property = waitProperty(name);
        if (Objects.nonNull(property)) {
            return (T) property;
        } else {
            return defaultValue;
        }
    }

    default <T> T waitProperty(String name, long timeout, T defaultValue) {
        final Object property = waitProperty(name, timeout);
        if (Objects.nonNull(property)) {
            return (T) property;
        } else {
            return defaultValue;
        }
    }
}
