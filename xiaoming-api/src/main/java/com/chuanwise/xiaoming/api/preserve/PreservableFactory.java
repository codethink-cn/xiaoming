package com.chuanwise.xiaoming.api.preserve;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 用于载入可存储数据的工厂类
 * @author Chuanwise
 */
public interface PreservableFactory<M> {
    /**
     * 从一个存储介质中载入数据
     * @param clazz 存储数据类对象
     * @param medium 存储介质
     * @param <T> 存储数据类
     * @return 载入的结果，或者抛出异常
     * @throws IOException 载入错误时
     */
    <T extends Preservable<M>> T loadThrowsException(Class<T> clazz, M medium) throws IOException;

    /**
     * 从一个存储介质中载入数据
     * @param clazz 存储数据类对象
     * @param medium 存储介质
     * @param <T> 存储数据类
     * @return 载入的结果。载入失败时返回 {@code null}
     */
    default <T extends Preservable<M>> T load(Class<T> clazz, M medium) {
        T result;
        try {
            result = loadThrowsException(clazz, medium);
        } catch (IOException exception) {
            exception.printStackTrace();
            result = null;
        }
        if (Objects.nonNull(result)) {
            result.setMedium(medium);
        }
        return result;
    }

    /**
     * 从一个存储介质中载入数据。载入失败时，使用 {@code supplier} 生产一个
     * @param clazz 存储数据类对象
     * @param medium 存储介质
     * @param supplier 生产者
     * @param <T> 存储数据类
     * @return
     */
    default <T extends Preservable<M>> T loadOrProduce(Class<T> clazz, M medium, Supplier<T> supplier) {
        T result = load(clazz, medium);
        if (Objects.isNull(result)) {
            result = Objects.requireNonNull(supplier.get());
        }
        result.setMedium(medium);
        return result;
    }
}
