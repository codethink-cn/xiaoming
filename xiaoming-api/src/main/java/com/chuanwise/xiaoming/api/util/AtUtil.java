package com.chuanwise.xiaoming.api.util;

import net.mamoe.mirai.message.code.MiraiCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtUtil {
    private static final Pattern AT_CATCODE_PATTERN = Pattern.compile("\\[mirai:at:(?<qq>\\d+)\\]");

    public static long parseQQ(String miraiCode) {
        final Matcher matcher = AT_CATCODE_PATTERN.matcher(miraiCode);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group("qq"));
        } else if (miraiCode.matches("\\d+")) {
            return Long.parseLong(miraiCode);
        } else {
            return -1;
        }
    }
}
