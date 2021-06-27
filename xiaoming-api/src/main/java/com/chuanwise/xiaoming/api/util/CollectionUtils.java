package com.chuanwise.xiaoming.api.util;

import java.util.*;
import java.util.function.Function;

public class CollectionUtils extends StaticUtils {
    /**
     * 将集合中的元素根据默认顺序，逐次使用 translator 函数处理后放入新的集合中
     * @param fromCollection 来源集合
     * @param toCollection 转化到的集合类型
     * @param translator 转化者
     * @param <F> 来源类型
     * @param <FC> 来源集合类型
     * @param <T> 转化类型
     * @param <TC> 转化集合类型
     * @return
     */
    public static <F, FC extends Collection<F>, T, TC extends Collection<T>> TC addTo(FC fromCollection, TC toCollection, Function<F, T> translator) {
        fromCollection.forEach(value -> toCollection.add(translator.apply(value)));
        return toCollection;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static <T> String getSummary(Iterable<T> iterable, Function<T, String> consumer, String prefix, String empty, String spliter) {
        final Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return empty;
        } else {
            StringBuilder builder = new StringBuilder(prefix);
            for (T t : iterable) {
                if (builder.length() != prefix.length()) {
                    builder.append(spliter);
                }
                builder.append(consumer.apply(t));
            }
            return builder.toString();
        }
    }

    public static <T> String getIndexSummary(Iterable<T> iterable, Function<T, String> consumer, String prefix, String empty, String spliter) {
        final Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return empty;
        } else {
            StringBuilder builder = new StringBuilder(prefix);
            int index = 1;
            for (T t : iterable) {
                if (builder.length() != prefix.length()) {
                    builder.append(spliter);
                }
                builder.append(index++).append("、").append(consumer.apply(t));
            }
            return builder.toString();
        }
    }

    public static <T> String getSummary(Iterable<T> iterable, Function<T, String> consumer, String prefix) {
        return getSummary(iterable, consumer, prefix, "（无）", "\n");
    }

    public static <T> String getSummary(Iterable<T> iterable, Function<T, String> consumer) {
        return getSummary(iterable, consumer, "", "（无）", "\n");
    }

    public static <T> String getSummary(Iterable<T> iterable) {
        return getSummary(iterable, Objects::toString);
    }

    public static <T> String getIndexSummary(Iterable<T> iterable, Function<T, String> consumer, String prefix) {
        return getIndexSummary(iterable, consumer, prefix, "（无）", "\n");
    }

    public static <T> String getIndexSummary(Iterable<T> iterable, Function<T, String> consumer) {
        return getIndexSummary(iterable, consumer, "", "（无）", "\n");
    }

    public static <T> String getIndexSummary(Iterable<T> iterable) {
        return getIndexSummary(iterable, Objects::toString);
    }

    public static <T> Set<T> asSet(T... elements) {
        return addTo(Arrays.asList(elements), new HashSet<>(elements.length), element -> element);
    }
}
