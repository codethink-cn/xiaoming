package cn.chuanwise.xiaoming.language.environment;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface VariableOperator<T> {
    Class<T> getClazz();

    List<VariableHandler<T>> getHandlers();

    default Object operate(T value, String variable) {
        Object result = null;
        for (VariableHandler<T> handler : getHandlers()) {
            if (handler.apply(value, variable)) {
                result = handler.onRequest(value, variable);
                if (Objects.nonNull(result)) {
                    return result;
                }
            }
        }
        return null;
    }

    default VariableOperator<T> register(VariableHandler<T> handler) {
        getHandlers().add(handler);
        return this;
    }

    VariableOperator<T> register(BiPredicate<T, String> predicate, BiFunction<T, String, Object> function);

    default VariableOperator<T> register(String identifier, Function<T, Object> function) {
        return register((t, string) -> Objects.equals(identifier, string), (t, string) -> function.apply(t));
    }

    default VariableOperator<T> register(String identifier, Object value) {
        return register((t, string) -> Objects.equals(identifier, string), (t, string) -> value);
    }

    boolean apply(T value, String identifier);
}
