package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentUtils extends StaticUtils {
    public static List<String> splitArgs(String line) {
        line = line.trim();

        ArrayList<String> result = new ArrayList<>();
        StringBuffer current = new StringBuffer();
        boolean isInArgument = false;
        int state = 0;

        for (int index = 0; index < line.length(); index++) {
            char ch = line.charAt(index);
            boolean spaceChar = Character.isSpaceChar(ch);

            switch (state) {
                case 0:
                    if (spaceChar) {
                        continue;
                    }
                    if (current.length() != 0) {
                        result.add(current.toString());
                        current.setLength(0);
                    }
                    if (ch == '\"') {
                        state = 3;
                        continue;
                    }
                    if (ch == '“') {
                        state = 4;
                        continue;
                    }
                    if (ch == '{') {
                        current.append(ch);
                        state = 5;
                        continue;
                    }
                    current.append(ch);
                    state = 2;
                    break;
                // 普通参数内部
                case 2:
                    if (spaceChar) {
                        state = 0;
                    }
                    else {
                        current.append(ch);
                    }
                    break;
                // 英文引号参数内部
                case 3:
                    if (ch == '\"') {
                        state = 0;
                    }
                    else {
                        current.append(ch);
                    }
                    break;
                // 中文引号参数内部
                case 4:
                    if (ch == '”') {
                        state = 0;
                    }
                    else {
                        current.append(ch);
                    }
                    break;
                // 英文大括号参数内部
                case 5:
                    current.append(ch);
                    if (ch == '}') {
                        state = 0;
                    }
                    break;
                default:
                    throw new XiaomingRuntimeException("illegal argument parse state: " + state);
            }
        }
        if (current.length() != 0) {
            result.add(current.toString());
            current.setLength(0);
        }
        return result;
    }

    public static String getReaminArgs(String[] arguments, int begin) {
        if (arguments.length == 0 || begin >= arguments.length) {
            return "";
        }
        if (begin == arguments.length - 1) {
            return arguments[begin];
        }
        StringBuilder builder = new StringBuilder(arguments[begin]);
        for (int index = begin + 1; index < arguments.length; index ++) {
            builder.append(" ").append(arguments[index]);
        }
        return builder.toString();
    }

    public static String getReaminArgs(List<String> arguments, int begin) {
        return getReaminArgs(arguments.toArray(new String[0]), begin);
    }

    public static String replaceArguments(String format, Object[] arguments) {
        StringBuilder builder = new StringBuilder(format);
        for (Object argument: arguments) {
            int pos = builder.indexOf("{}");
            if (pos != -1) {
                builder.replace(pos, pos + 2, Objects.isNull(argument) ? "null" : argument.toString());
            }
            else {
                break;
            }
        }
        return builder.toString();
    }

    protected static final Pattern VARIABLE_REFERENCE = Pattern.compile("\\{(?<identify>[\\S\\s]+?)\\}");
    private static final Random RANDOM = new Random();
    public static String replaceArguments(String format, Map<String, ? extends Object> environment, int maxIterateTime) {
        Matcher matcher = VARIABLE_REFERENCE.matcher(format);
        StringBuilder builder = new StringBuilder(format);

        int times = 0;
        int pos = 0;
        while (matcher.find(pos) && times < maxIterateTime) {
            int start = matcher.start();
            int end = matcher.end();
            String identify = matcher.group("identify");
            Object value = environment.get(identify);
            String string = identify;

            // 集合就随机选择一个幸运成员。只有 Collection<String> 的成员会被特殊对待
            if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
                final Object tempValue = ((Collection<?>) value).toArray(new Object[0])[RANDOM.nextInt(((Collection<?>) value).size())];
                if (tempValue instanceof String) {
                    value = tempValue;
                }
            }
            if (Objects.nonNull(value)) {
                string = value instanceof String ? ((String) value) : value.toString();
                builder.replace(matcher.start(), matcher.end(), string);
            }
            matcher = VARIABLE_REFERENCE.matcher(builder);

            pos = Objects.nonNull(value) ? start : end;
            times++;
        }
        return builder.toString();
    }
//
//    protected static Set<Class<?>> classes = new CopyOnWriteArraySet<>();
//    public static Map<String, Object> makeEnvironment(Object object) {
//        if (Objects.isNull(object)) {
//            return null;
//        }
//        synchronized (classes) {
//            classes.clear();
//            return makeEnvironment(object, "");
//        }
//    }
//
//    protected static Map<String, Object> makeEnvironment(Object object, String prefix) {
//        if (Objects.isNull(object)) {
//            return null;
//        }
//
//        final Class<?> clazz = object.getClass();
//        if (classes.contains(clazz)) {
//            return null;
//        } else {
//            classes.add(clazz);
//        }
//        Map<String, Object> environment = new HashMap<>();
//        for (Field field : clazz.getDeclaredFields()) {
//            final boolean accessible = field.isAccessible();
//            field.setAccessible(true);
//
//            try {
//                final Object value = field.get(object);
//                if (Objects.nonNull(value) && !(value instanceof String)) {
//                    final Map<String, Object> map = makeEnvironment(value, prefix + (StringUtils.isEmpty(prefix) ? "" : ".") + field.getName());
//                    if (Objects.nonNull(map)) {
//                        environment.putAll(map);
//                    }
//                }
//                environment.put(prefix + field.getName(), value);
//            } catch (IllegalAccessException illegalAccessException) {
//                illegalAccessException.printStackTrace();
//            }
//
//            field.setAccessible(accessible);
//        }
//
//        return environment;
//    }
}