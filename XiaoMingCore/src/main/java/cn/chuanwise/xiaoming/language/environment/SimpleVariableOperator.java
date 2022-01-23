package cn.chuanwise.xiaoming.language.environment;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.language.variable.VariableOperator;
import cn.chuanwise.xiaoming.language.variable.VariableRequester;
import cn.chuanwise.xiaoming.object.PluginObjectImpl;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@Data
public class SimpleVariableOperator<T> extends PluginObjectImpl implements VariableOperator<T> {
    final List<VariableRequester<T>> handlers = new ArrayList<>();
    final Class<T> clazz;

    @Override
    public boolean apply(T value, String identifier) {
        return Objects.nonNull(CollectionUtil.first(handlers, handler -> handler.apply(value, identifier)));
    }

    @Override
    public VariableOperator<T> addOperator(BiPredicate<T, String> predicate, BiFunction<T, String, Object> function) {
        return addOperator(new VariableRequester<T>() {
            @Override
            public Object request(T value, String identifier) {
                return function.apply(value, identifier);
            }

            @Override
            public boolean apply(T value, String identifier) {
                return predicate.test(value, identifier);
            }
        });
    }
}
