package com.chuanwise.xiaoming.api.util;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeUtil {
    private TimeUtil() {}

    public static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final long SECOND_MINS = 1000;
    public static final long MINUTE_MINS = SECOND_MINS * 60;
    public static final long HOUR_MINS = MINUTE_MINS * 60;
    public static final long DAY_MINS = HOUR_MINS * 24;
    public static final long MOUTH_MINS = DAY_MINS * 30;

    @NotNull
    public static String toTimeString(long time) {
        long mouth = time / MOUTH_MINS;
        time -= mouth * MOUTH_MINS;
        long days = time / DAY_MINS;
        time -= days * DAY_MINS;
        long hours = time / HOUR_MINS;
        time -= hours * HOUR_MINS;
        long minutes = time / MINUTE_MINS;
        time -= minutes * MINUTE_MINS;
        long seconds = time / SECOND_MINS;
        time -= seconds * SECOND_MINS;

        StringBuilder result = new StringBuilder();
        if (mouth > 0) {
            result.append(days + "月");
        }
        if (days > 0) {
            result.append(days + "天");
        }
        if (hours > 0) {
            result.append(hours + "小时");
        }
        if (minutes > 0) {
            result.append(minutes + "分钟");
        }
        if (result.length() == 0 && seconds > 0) {
            result.append(seconds + "秒");
        }
        if (result.length() == 0 && time > 0) {
            result.append(time + "毫秒");
        }
        return result.toString();
    }

    public static long parseTime(final String timeString) {
        if (!timeString.matches("(\\d+[Dd天Hh时Mm分Ss秒])+")) {
            return -1;
        }
        long totalTime = 0;
        long currentNumber = 0;
        for (int index = 0; index < timeString.length(); index ++) {
            if (Character.isDigit(timeString.charAt(index))) {
                currentNumber *= 10;
                currentNumber += timeString.charAt(index) - '0';
                continue;
            }
            if ("Dd天".contains("" + timeString.charAt(index))) {
                totalTime += DAY_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if ("Hh时".contains("" + timeString.charAt(index))) {
                totalTime += HOUR_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if ("Mm分".contains("" + timeString.charAt(index))) {
                totalTime += MINUTE_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if ("Ss秒".contains("" + timeString.charAt(index))) {
                totalTime += SECOND_MINS * currentNumber;
                currentNumber = 0;
                continue;
            }
            if (Character.isSpaceChar(timeString.charAt(index))) {
                continue;
            }
            return -1;
        }
        return totalTime;
    }

    @NotNull
    public static String after(final long time) {
        return after(System.currentTimeMillis(), time);
    }

    @NotNull
    public static String after(final long from, final long time) {
        return toTimeString(time) + "后（" + FORMAT.format(from + time) + "）";
    }
}
