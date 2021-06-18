package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends StaticUtils {
    /**
     * 提取指令参数时的正则表达式
     */
    public static final Pattern PARAMETER_REGEX = Pattern.compile("\\((?<fst>[^|)]+).*?\\)");


    public static boolean isEmpty(String string) {
        return Objects.isNull(string) || string.isEmpty();
    }

    public static String stringOr(String string, String or) {
        return isEmpty(string) ? or : string;
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

    public static String translateUsageRegex(String format) {
        final StringBuilder builder = new StringBuilder(format
                .replaceAll(Pattern.quote("\\s+"), "  ")
                .replaceAll(Pattern.quote("(?"), "")
                .replaceAll(Pattern.quote(ParameterFilterMatcher.NORMAL_VARIABLE_REGEX + ")"), "")
                .replaceAll(Pattern.quote(ParameterFilterMatcher.REMAIN_VARIABLE_REGEX + ")"), "")
                .replaceAll(Pattern.quote("\\[mirai:at:"), "@")
                .replaceAll(Pattern.quote("\\]"), ""));

        Matcher matcher = PARAMETER_REGEX.matcher(builder);
        while (matcher.find()) {
            builder.replace(matcher.start(), matcher.end(), matcher.group("fst"));
            matcher = PARAMETER_REGEX.matcher(builder);
        }
        return builder.toString();
    }
}
