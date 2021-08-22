package cn.chuanwise.xiaoming.language;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.language.convertor.Convertor;
import cn.chuanwise.xiaoming.language.convertor.ConvertorHandler;
import cn.chuanwise.xiaoming.language.sentence.LanguageRenderContext;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.language.variable.VariableGetter;
import cn.chuanwise.xiaoming.language.variable.VariableHandler;
import cn.chuanwise.xiaoming.language.variable.VariableOperator;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface LanguageManager extends ModuleObject {
    File getDirectory();

    /** 语言库 */
    List<Language> getLanguages();

    void registerLanguage(Language value, Plugin plugin);

    void unregisterLanguages(Plugin plugin);

    default List<Language> getLanguages(Plugin plugin) {
        return CollectionUtility.filter(getLanguages(), new ArrayList<>(), language -> (language.getPlugin() == plugin));
    }

    default Sentence getSentence(String identifier) {
        for (Language language : getLanguages()) {
            final Sentence sentence = language.getSentence(identifier);
            if (Objects.nonNull(sentence)) {
                return sentence;
            }
        }
        return null;
    }

    default String getSentenceValue(String identifier) {
        return getSentenceValueOrDefault(identifier, identifier);
    }

    default String getSentenceValueOrDefault(String identifier, String defaultValue) {
        final Sentence sentence = getSentence(identifier);
        if (Objects.isNull(sentence)) {
            return defaultValue;
        } else {
            return sentence.getValue();
        }
    }

    /** 全局变量表 */
    Map<String, VariableHandler> getVariables();

    default List<VariableHandler> getVariables(Plugin plugin) {
        return CollectionUtility.filter(getVariables().values(), new ArrayList<>(), variable -> (variable.getPlugin() == plugin));
    }

    void registerVariable(String name, VariableGetter<?> getter, Plugin plugin);

    default void registerVariable(String name, Object value, Plugin plugin) {
        registerVariable(name, () -> value, plugin);
    }

    void unregisterVariables(Plugin plugin);

    default VariableHandler getGlobalVariableHandler(String name) {
        return getVariables().get(name);
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

    default String formatAdditional(Sentence sentence, Function<String, ?> getter, Object... contexts) {
        return formatContext(sentence.getValue(), getter, new LanguageRenderContext(sentence.getDefaultValueContextParameters(), contexts));
    }

    default String format(String format, Object... contexts) {
        return formatAdditional(format, variable -> null, contexts);
    }

    default String format(Sentence sentence, Object... contexts) {
        return formatAdditional(sentence.getValue(), variable -> null, contexts);
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
        return CollectionUtility.first(getConvertors(), convertor -> convertor.getFromClass().isAssignableFrom(clazz));
    }

    default List<ConvertorHandler> getConvertors(Plugin plugin) {
        return CollectionUtility.filter(getConvertors(), new ArrayList<>(), convertor -> (convertor.getPlugin() == plugin));
    }

    <T> void registerConvertor(Class<T> fromClass, Convertor<T> convertor, Plugin plugin);

    void unregisterConvertors(Plugin plugin);

    /** 变量运算器 */
    List<VariableOperator<?>> getOperators();

    <T> VariableOperator<T> registerOperators(Class<T> clazz, Plugin plugin);

    default List<VariableOperator<?>> getOperators(Plugin plugin) {
        return CollectionUtility.filter(getOperators(), new ArrayList<>(), operator -> (operator.getPlugin() == plugin));
    }

    void unregisterOperators(Plugin plugin);

    /** 变量演算 */
    default Object caculate(Function<String, Object> getter, String variable) {
        final int dot = variable.indexOf(".");
        if (dot != -1) {
            return caculate(getter.apply(variable.substring(0, dot)), variable.substring(dot + 1));
        } else {
            return Objects.toString(getter.apply(variable), null);
        }
    }

    Object caculate(Object object, String variable);

    default void unregisterPlugin(Plugin plugin) {
        unregisterConvertors(plugin);
        unregisterVariables(plugin);
        unregisterLanguages(plugin);
        unregisterOperators(plugin);
    }
}
