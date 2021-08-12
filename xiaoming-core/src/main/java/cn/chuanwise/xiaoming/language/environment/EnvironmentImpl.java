package cn.chuanwise.xiaoming.language.environment;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
public class EnvironmentImpl extends ModuleObjectImpl implements Environment {
    final List<VariableOperator<?>> operators = new ArrayList<>();

    public EnvironmentImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public Object render(Object object, String variable) {
        if (Objects.isNull(object)) {
            return null;
        }
        final String[] paths = variable.split(Pattern.quote("."));

        // object 是当前值
        Object value = object;
        boolean operated = false;
        for (int i = 0; i < paths.length; i++) {
            final String current = paths[i];

            // 寻找一种类型的操作集合，进行所有操作
            operated = false;
            for (VariableOperator operator : forOperators(value.getClass())) {
                if (operator.apply(value, current)) {
                    final Object nextValue = operator.operate(value, current);
                    if (Objects.nonNull(nextValue)) {
                        value = nextValue;
                        operated = true;
                        break;
                    }
                }
            }

            // 如果找不到任何操作，就返回
            if (!operated) {
                break;
            }
        }

        if (operated) {
            return value;
        } else {
            return null;
        }
    }

    public <T> List<VariableOperator<T>> forOperators(Class<T> clazz) {
        final List<VariableOperator<T>> results = new ArrayList<>();
        for (VariableOperator<?> operator : operators) {
            if (operator.getClazz().isAssignableFrom(clazz)) {
                results.add((VariableOperator<T>) operator);
            }
        }
        return results;
    }

    @Override
    public <T> VariableOperator<T> register(Class<T> clazz) {
        final SimpleVariableOperator<T> operator = new SimpleVariableOperator<>(clazz);
        operators.add(operator);
        return operator;
    }
}
