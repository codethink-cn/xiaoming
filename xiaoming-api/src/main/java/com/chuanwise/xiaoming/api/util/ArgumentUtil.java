package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class ArgumentUtil {
    private ArgumentUtil() {}

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

    public static String getReaminArgs(List<String> args, int begin) {
        if (args.isEmpty() || begin >= args.size()) {
            return "";
        }
        if (begin == args.size() - 1) {
            return args.get(begin);
        }
        StringBuilder builder = new StringBuilder(args.get(begin));
        for (int index = begin + 1; index < args.size(); index ++) {
            builder.append(" ").append(args.get(index));
        }
        return builder.toString();
    }

    public static String replaceArguments(String format, Object[] arguments) {
        StringBuilder builder = new StringBuilder(format);
        for (Object argument: arguments) {
            int pos = builder.indexOf("{}");
            if (pos != -1) {
                builder.replace(pos, pos + 2, argument.toString());
            }
            else {
                break;
            }
        }
        return builder.toString();
    }
}
