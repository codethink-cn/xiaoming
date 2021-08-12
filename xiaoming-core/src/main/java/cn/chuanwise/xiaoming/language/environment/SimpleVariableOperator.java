package cn.chuanwise.xiaoming.language.environment;

import cn.chuanwise.utility.CollectionUtility;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
public class SimpleVariableOperator<T> implements VariableOperator<T> {
    final List<VariableHandler<T>> handlers = new ArrayList<>();
    final Class<T> clazz;

    @Override
    public boolean apply(T value, String identifier) {
        return Objects.nonNull(CollectionUtility.first(handlers, handler -> handler.apply(value, identifier)));
    }

    @Override
    public VariableOperator<T> register(BiPredicate<T, String> predicate, BiFunction<T, String, Object> function) {
        return register(new VariableHandler<T>() {
            @Override
            public Object onRequest(T value, String identifier) {
                return function.apply(value, identifier);
            }

            @Override
            public boolean apply(T value, String identifier) {
                return predicate.test(value, identifier);
            }
        });
    }
}
