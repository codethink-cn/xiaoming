package cn.chuanwise.xiaoming.language;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.language.convertor.Convertor;
import cn.chuanwise.xiaoming.language.convertor.ConvertorHandler;
import cn.chuanwise.xiaoming.language.variable.VariableGetter;
import cn.chuanwise.xiaoming.language.variable.VariableHandler;
import cn.chuanwise.xiaoming.language.variable.VariableOperator;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.*;
import java.util.function.Function;

public interface LanguageManager extends ModuleObject {
    /** 全局变量表 */
    Map<String, VariableHandler> getGlobalVariables();

    default List<VariableHandler> getGlobalVariables(Plugin plugin) {
        return CollectionUtil.filter(getGlobalVariables().values(), new ArrayList<>(), variable -> (variable.getPlugin() == plugin));
    }

    void registerVariable(String name, VariableGetter<?> getter, Plugin plugin);

    default void registerVariable(String name, Object value, Plugin plugin) {
        registerVariable(name, () -> value, plugin);
    }

    void unregisterVariables(Plugin plugin);

    default VariableHandler getGlobalVariableHandler(String name) {
        return getGlobalVariables().get(name);
    }

    default Object getGlobalVariable(String name) {
        final VariableHandler handler = getGlobalVariableHandler(name);
        if (Objects.nonNull(handler)) {
            return handler.getGetter().get();
        } else {
            return null;
        }
    }

    /** 按照给定的格式字符串格式化变量 */
    String formatContext(String format, Function<String, ?> getter, LanguageRenderContext context);

    String formatAdditional(String format, Function<String, ?> getter, Object... contexts);

    default String format(String format, Object... contexts) {
        return formatAdditional(format, variable -> null, contexts);
    }

    /** 字符串转换器 */
    List<ConvertorHandler> getConvertors();

    default <T> String convert(T object) {
        if (Objects.isNull(object)) {
            return null;
        }

        final ConvertorHandler<T> convertor = (ConvertorHandler<T>) getConvertor(object.getClass());
        if (Objects.isNull(convertor)) {
            return Objects.toString(object);
        } else {
            return convertor.getConvertor().convert(((T) object));
        }
    }

    default <T> ConvertorHandler<T> getConvertor(Class<T> clazz) {
        return CollectionUtil.first(getConvertors(), convertor -> convertor.getFromClass().isAssignableFrom(clazz));
    }

    default List<ConvertorHandler> getConvertors(Plugin plugin) {
        return CollectionUtil.filter(getConvertors(), new ArrayList<>(), convertor -> (convertor.getPlugin() == plugin));
    }

    <T> void registerConvertor(Class<T> fromClass, Convertor<T> convertor, Plugin plugin);

    void unregisterConvertors(Plugin plugin);

    /** 变量运算器 */
    List<VariableOperator<?>> getOperators();

    <T> VariableOperator<T> registerOperators(Class<T> clazz, Plugin plugin);

    default List<VariableOperator<?>> getOperators(Plugin plugin) {
        return CollectionUtil.filter(getOperators(), new ArrayList<>(), operator -> (operator.getPlugin() == plugin));
    }

    void unregisterOperators(Plugin plugin);

    /** 变量演算 */
    default Object caculate(Function<String, Object> getter, String variable) {
        final int dot = variable.indexOf(".");
        if (dot != -1) {
            return caculate(getter.apply(variable.substring(0, dot)), variable.substring(dot + 1));
        } else {
            // 2022.1.2 删除 toString
            return getter.apply(variable);
//            return Objects.toString(getter.apply(variable), null);
        }
    }

    Object caculate(Object object, String variable);

    default void unregisterPlugin(Plugin plugin) {
        unregisterConvertors(plugin);
        unregisterVariables(plugin);
        unregisterOperators(plugin);
    }
}
