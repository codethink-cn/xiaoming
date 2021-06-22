package com.chuanwise.xiaoming.host.log;

import com.chuanwise.xiaoming.api.util.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UncolorPatternLayout extends PatternLayout {
    static final Pattern COLOR_PATTERN = Pattern.compile("\\$(?<color>\\w+)");

    String color(String name) {
        return "";
    }

    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    Map<String, String> classNameMap = new HashMap<>();

    @Override
    public String format(LoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        // [2021-09-08 23:09:15] [main] 信息
        builder.append(color("gray")).append("[")
                .append(color("cyan")).append(DATE_FORMAT.format(event.getTimeStamp()))
                .append(color("gray")).append("] ")

                .append(color("gray")).append("[")
                .append(color("green")).append(event.getThreadName())
                .append(color("gray")).append("] ");

        // [INFO] 信息
        builder.append(color("gray")).append("[");
        appendLevelColor(event.getLevel(), builder)
                .append(event.getLevel().toString())
                .append(color("gray")).append("] ");
        appendLevelColor(event.getLevel(), builder)
                .append(" ");

        // 获得缩减后的当前类名
        final String shortClassName = requireShortClassName(event.getLoggerName());
        final int currentLength = builder.length() + shortClassName.length();
        // maxLogHeadLength = Math.max(maxLogHeadLength, currentLength);

        // 填充对齐使用的空格
        builder.append(StringUtils.getSpaceString(maxLogHeadLength - currentLength))
                .append(shortClassName)
                .append(color("gray"))
                .append(" : ");

        // 日志主体内容
        appendLevelColor(event.getLevel(), builder)
                .append(event.getMessage());

        // 结尾的换行
        builder.append("\r\n");
        return builder.toString();
    }

    StringBuilder appendLevelColor(Level level, StringBuilder builder) {
        switch (level.toInt()) {
            case Level.INFO_INT:
                builder.append(color("cyan"));
                break;
            case Level.WARN_INT:
                builder.append(color("yellow"));
                break;
            case Level.ERROR_INT:
                builder.append(color("red"));
                break;
            default:
                builder.append(color("cyan"));
                break;
        }
        return builder;
    }

    static final Pattern PACKAGE_NAME = Pattern.compile("(?<package>\\w\\w+)\\.");

    int maxLogHeadLength = 10;

    String requireShortClassName(String clazzName) {
        String result = classNameMap.get(clazzName);
        if (Objects.isNull(result)) {
            StringBuilder builder = new StringBuilder(clazzName);

            // 缩减包名
            Matcher matcher = PACKAGE_NAME.matcher(builder);
            while (matcher.find()) {
                final String packageName = matcher.group("package");
                builder.replace(matcher.start(), matcher.end(), packageName.charAt(0) + ".");
                matcher = PACKAGE_NAME.matcher(builder);
            }

            // 记载这个类名
            classNameMap.put(clazzName, builder.toString());
            result = clazzName;
        }
        return result;
    }
}