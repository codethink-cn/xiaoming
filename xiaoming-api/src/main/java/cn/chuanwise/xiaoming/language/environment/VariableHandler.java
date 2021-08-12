package cn.chuanwise.xiaoming.language.environment;

import java.util.Objects;

@FunctionalInterface
public interface VariableHandler<T> {
    Object onRequest(T value, String identifier);

    default boolean apply(T value, String identifier) {
        return true;
    }
}
