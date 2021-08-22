package cn.chuanwise.xiaoming.utility;

import cn.chuanwise.utility.*;
import cn.chuanwise.xiaoming.configuration.CollectionFormat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class CollectionFormatUtility extends StaticUtility {
    public static <T> String format(CollectionFormat format, Collection<T> collection, Function<T, Map<String, ? extends Object>> environmentBuilder, int maxIteration) {
        Map<String, Object> commonEnvironment = new HashMap<>();
        commonEnvironment.put("size", collection.size());
        commonEnvironment.put("index", 0);

        return ArgumentUtility.format(ObjectUtility.firstNonNull(format.getPrefix(), "") + CollectionUtility.toIndexString(collection, (index, element) -> {
            commonEnvironment.put("index", index + 1);
            return "";
        }, element -> {
            if (Objects.isNull(element)) {
                return format.getNullObject();
            }

            final Map<String, ? extends Object> specialEnvironment = environmentBuilder.apply(element);
            final String content = ArgumentUtility.format(format.getContent(), maxIteration, specialEnvironment);

            return ArgumentUtility.format(content, maxIteration, commonEnvironment);
        }, ObjectUtility.firstNonNull(format.getSplitter(), "")) +
                ObjectUtility.firstNonNull(format.getSuffix(), ""), maxIteration, commonEnvironment);
    }
}