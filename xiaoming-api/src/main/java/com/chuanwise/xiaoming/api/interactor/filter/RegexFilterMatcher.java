package com.chuanwise.xiaoming.api.interactor.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Data
public abstract class RegexFilterMatcher extends FilterMatcher {
    /**
     * 提取指令参数时的正则表达式
     */
    public static final Pattern PARAMETER_REGEX = Pattern.compile("\\((?<fst>[^|)]+).*?\\)");

    Pattern pattern;

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(pattern.pattern()
                .replaceAll(Pattern.quote("\\s+"), "  ")
                .replaceAll(Pattern.quote("(?"), "")
                .replaceAll(Pattern.quote(ParameterFilterMatcher.NORMAL_VARIABLE_REGEX + ")"), "")
                .replaceAll(Pattern.quote(ParameterFilterMatcher.REMAIN_VARIABLE_REGEX + ")"), "")
                .replaceAll(Pattern.quote("\\[mirai:at:"), "@")
                .replaceAll(Pattern.quote("\\]"), ""));
        while (true) {
            final Matcher matcher = PARAMETER_REGEX.matcher(builder);
            if (matcher.find()) {
                builder.replace(matcher.start(), matcher.end(), matcher.group("fst"));
            } else {
                break;
            }
        }
        return builder.toString();
    }
}
