package com.chuanwise.xiaoming.api.util;

import java.util.Collection;
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
}
