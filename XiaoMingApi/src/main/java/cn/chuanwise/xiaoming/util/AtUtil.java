package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.NumberUtil;
import cn.chuanwise.util.StaticUtil;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtUtil extends StaticUtil {
    private static final Pattern AT_CATCODE_PATTERN = Pattern.compile("\\[mirai:at:(?<qq>\\d+)\\]");

    public static Optional<Long> parseAt(String miraiCode) {
        final Matcher matcher = AT_CATCODE_PATTERN.matcher(miraiCode);
        if (matcher.matches()) {
            return Optional.of(Long.parseLong(matcher.group("qq")));
        } else {
            final Optional<Long> optionalCode = NumberUtil.parseLong(miraiCode);
            if (!optionalCode.isPresent() || optionalCode.get() < 0) {
                return Optional.empty();
            } else {
                return optionalCode;
            }
        }
    }
}
