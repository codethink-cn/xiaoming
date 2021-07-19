package cn.chuanwise.xiaoming.api.utility;

import cn.chuanwise.utility.StaticUtility;
import cn.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsageStringUtility extends StaticUtility {
    /**
     * 提取指令参数时的正则表达式
     */
    public static final Pattern PARAMETER_REGEX = Pattern.compile("\\((?<fst>[^|)]+).*?\\)");

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
