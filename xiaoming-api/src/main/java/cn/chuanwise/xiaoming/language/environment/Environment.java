package cn.chuanwise.xiaoming.language.environment;

import cn.chuanwise.toolkit.value.container.BiValueContainer;
import cn.chuanwise.utility.MapUtility;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface Environment {
    default Object render(Function<String, Object> getter, String variable) {
        final int dot = variable.indexOf(".");
        if (dot != -1) {
            return render(getter.apply(variable.substring(0, dot)), variable.substring(dot + 1));
        } else {
            return Objects.toString(getter.apply(variable), null);
        }
    }

    Object render(Object object, String variable);

    default <T> void register(Class<T> clazz, VariableOperator<T> operator) {
        getOperators().add(operator);
    }

    <T> VariableOperator<T> register(Class<T> clazz);

    List<VariableOperator<?>> getOperators();
}
