package com.chuanwise.xiaoming.api.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

/**
 * @author Chuanwise
 */
public class ArrayUtils {
    /**
     * 将一个数组内的所有元素经过一个方法处理后复制进新的数组
     * @param froms 来源数组
     * @param clazz 新数组元素类型
     * @param translator 转换器
     * @param <F> 来源类型
     * @param <T> 新类型
     * @return 新的数组，T[] 类型
     */
    public static <F, T> T[] copyAs(F[] froms, Class<T> clazz, Function<F, T> translator) {
        T[] result = (T[]) Array.newInstance(clazz, froms.length);
        for (int i = 0; i < result.length; i++) {
            result[i] = translator.apply(froms[i]);
        }
        return result;
    }
}
