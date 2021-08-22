package cn.chuanwise.xiaoming.utility;

import cn.chuanwise.utility.NumberUtility;
import cn.chuanwise.utility.StaticUtility;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtUtility extends StaticUtility {
    private static final Pattern AT_CATCODE_PATTERN = Pattern.compile("\\[mirai:at:(?<qq>\\d+)\\]");

    public static Long parseAt(String miraiCode) {
        final Matcher matcher = AT_CATCODE_PATTERN.matcher(miraiCode);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group("qq"));
        } else {
            final Long parseResult = NumberUtility.parseLong(miraiCode);
            if (Objects.isNull(parseResult) || parseResult <= 0) {
                return null;
            } else {
                return parseResult;
            }
        }
    }
}
