package cn.chuanwise.xiaoming.language.variable;

import cn.chuanwise.xiaoming.object.PluginObject;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface VariableOperator<T> extends PluginObject {
    Class<T> getClazz();

    List<VariableRequester<T>> getHandlers();

    default Object operate(T value, String variable) {
        Object result = null;
        for (VariableRequester<T> handler : getHandlers()) {
            if (handler.apply(value, variable)) {
                result = handler.request(value, variable);
                if (Objects.nonNull(result)) {
                    return result;
                }
            }
        }
        return null;
    }

    default VariableOperator<T> addOperator(VariableRequester<T> handler) {
        getHandlers().add(handler);
        return this;
    }

    VariableOperator<T> addOperator(BiPredicate<T, String> predicate, BiFunction<T, String, Object> function);

    default VariableOperator<T> addOperator(String identifier, Function<T, Object> function) {
        return addOperator((t, string) -> Objects.equals(identifier, string), (t, string) -> function.apply(t));
    }

    default VariableOperator<T> addOperator(String identifier, Object value) {
        return addOperator((t, string) -> Objects.equals(identifier, string), (t, string) -> value);
    }

    boolean apply(T value, String identifier);
}
