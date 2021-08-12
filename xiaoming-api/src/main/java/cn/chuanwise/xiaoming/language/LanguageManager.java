package cn.chuanwise.xiaoming.language;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.FunctionalUtility;
import cn.chuanwise.xiaoming.language.environment.Environment;
import cn.chuanwise.xiaoming.language.environment.VariableOperator;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public interface LanguageManager extends ModuleObject {
    File getDirectory();

    Environment getEnvironment();

    Map<String, Supplier<Object>> getVariables();

    default void registerLanguage(Language value, XiaomingPlugin plugin) {
        getLanguages().add(value);
    }

    List<Language> getLanguages();

    default Sentence getSentence(String identifier) {
        for (Language language : getLanguages()) {
            final Sentence sentence = language.forSentence(identifier);
            if (Objects.nonNull(sentence)) {
                return sentence;
            }
        }
        return null;
    }

    default String getSentenceValue(String identifier) {
        return getSentenceValueOrDefault(identifier, "undefined");
    }

    default String getSentenceValueOrDefault(String identifier, String defaultValue) {
        final Sentence sentence = getSentence(identifier);
        if (Objects.isNull(sentence)) {
            return defaultValue;
        } else {
            return sentence.getValue();
        }
    }

    default void registerVariable(String name, Supplier<Object> getter, XiaomingPlugin plugin) {
        getVariables().put(name, getter);
    }

    default void registerVariable(String name, Object value, XiaomingPlugin plugin) {
        getVariables().put(name, () -> value);
    }

    default Supplier<Object> forGlobalVariableSupplier(String name) {
        return getVariables().get(name);
    }

    default Object forGlobalVariable(String name) {
        final Supplier<Object> supplier = forGlobalVariableSupplier(name);
        return FunctionalUtility.runIfFunctionNonNull(supplier);
    }

    default String render(String format) {
        return render(format, variable -> null);
    }

    default String render(String format, Function<String, Object> getter) {
        return ArgumentUtility.render(format, getXiaomingBot().getConfiguration().getMaxIterateTime(), completeVariableName -> {
            return getEnvironment().render(baseVariableName -> {
                Object value = getter.apply(baseVariableName);
                if (Objects.isNull(value)) {
                    return forGlobalVariable(baseVariableName);
                } else {
                    return value;
                }
            }, completeVariableName);
        }, this::convertToString);
    }

    default <T> String convertToString(T object) {
        if (Objects.isNull(object)) {
            return null;
        }

        final StringConvertor<T> convertor = (StringConvertor<T>) forStringConvertor(object.getClass());
        if (Objects.isNull(convertor)) {
            return Objects.toString(object);
        } else {
            return convertor.convert(((T) object));
        }
    }

    default <T> StringConvertor<T> forStringConvertor(Class<T> clazz) {
        return CollectionUtility.first(getStringConvertors(), convertor -> convertor.getClazz().isAssignableFrom(clazz));
    }

    default String render(Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        return render(sentence.getValue(), externalGetter, arguments);
    }

    /**
     * 按照给定的格式字符串格式化变量
     * @param format 格式字符串
     * @param externalGetter 外界变量获取
     * @param arguments 参数
     * @return 替换后的字符串
     */
    String render(String format, Function<String, Object> externalGetter, Object... arguments);

    List<StringConvertor> getStringConvertors();

    default <T> void registerConvertor(Class<T> clazz, Function<T, String> convertor, XiaomingPlugin plugin) {
        getStringConvertors().add(new StringConvertor<T>(clazz) {
            @Override
            public String convert(T value) {
                return convertor.apply(value);
            }
        });
    }

    default <T> void registerConvertor(StringConvertor<T> convertor, XiaomingPlugin plugin) {
        getStringConvertors().add(convertor);
    }

    default <T> VariableOperator<T> registerOperator(Class<T> clazz, XiaomingPlugin plugin) {
        return getEnvironment().register(clazz);
    }
}
