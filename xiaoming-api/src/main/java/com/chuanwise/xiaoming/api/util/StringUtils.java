package com.chuanwise.xiaoming.api.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class StringUtils extends StaticUtils {
    public static boolean isEmpty(String string) {
        return Objects.isNull(string) || string.isEmpty();
    }

    /**
     * 获得一定长度的空格串
     * @param length 长度
     * @return 生成的空格字符串
     */
    public static String getSpaceString(int length) {
        return getRepeatString(length, ' ');
    }

    /**
     * 获得一定长度的重复字符字符串
     * @param length 长度
     * @param ch 重复的字符
     * @return 生成的字符串
     */
    public static String getRepeatString(int length, char ch) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }
}
