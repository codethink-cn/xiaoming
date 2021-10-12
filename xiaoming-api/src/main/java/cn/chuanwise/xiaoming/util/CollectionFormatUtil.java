package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.*;
import cn.chuanwise.xiaoming.configuration.CollectionFormat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class CollectionFormatUtil extends StaticUtil {
    public static <T> String format(CollectionFormat format, Collection<T> collection, Function<T, Map<String, ? extends Object>> environmentBuilder, int maxIteration) {
        Map<String, Object> commonEnvironment = new HashMap<>();
        commonEnvironment.put("size", collection.size());
        commonEnvironment.put("index", 0);

        return ArgumentUtil.format(ObjectUtil.firstNonNull(format.getPrefix(), "") + CollectionUtil.toIndexString(collection, (index, element) -> {
            commonEnvironment.put("index", index + 1);
            return "";
        }, element -> {
            if (Objects.isNull(element)) {
                return format.getNullObject();
            }

            final Map<String, ? extends Object> specialEnvironment = environmentBuilder.apply(element);
            final String content = ArgumentUtil.format(format.getContent(), maxIteration, specialEnvironment);

            return ArgumentUtil.format(content, maxIteration, commonEnvironment);
        }, ObjectUtil.firstNonNull(format.getSplitter(), "")) +
                ObjectUtil.firstNonNull(format.getSuffix(), ""), maxIteration, commonEnvironment);
    }
}