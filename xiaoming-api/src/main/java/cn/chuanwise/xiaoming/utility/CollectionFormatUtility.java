package cn.chuanwise.xiaoming.utility;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.StaticUtility;
import cn.chuanwise.xiaoming.configuration.CollectionFormat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CollectionFormatUtility extends StaticUtility {
    public static <T> String format(CollectionFormat format, Collection<T> collection, Function<T, Map<String, ? extends Object>> environmentBuilder, int maxIteration) {
        Map<String, Object> commonEnvironment = new HashMap<>();
        commonEnvironment.put("size", collection.size());

        return format.getPrefix() + CollectionUtility.toIndexString(collection, (index, element) -> {
            index++;
            return format.getIndex().replaceAll(Pattern.quote("{index}"), String.valueOf(index));
        }, element -> {
            if (Objects.isNull(element)) {
                return format.getNull();
            }

            final Map<String, ? extends Object> specialEnvironment = environmentBuilder.apply(element);
            final String content = ArgumentUtility.replaceArguments(format.getContent(), specialEnvironment, maxIteration);

            return ArgumentUtility.replaceArguments(content, commonEnvironment, maxIteration);
        }, format.getSplitter()) + format.getSuffix();
    }
}
