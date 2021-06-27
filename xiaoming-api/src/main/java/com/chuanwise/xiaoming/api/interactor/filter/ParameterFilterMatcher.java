package com.chuanwise.xiaoming.api.interactor.filter;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ParameterFilterMatcher extends RegexFilterMatcher {
    public static final String NORMAL_VARIABLE_REGEX = "\\S+";
    public static final String REMAIN_VARIABLE_REGEX = "[\\s\\S]+";
    public static final String SPACING = "\\s+";
    Set<String> parameterNames;
    Map<Long, Matcher> matchers = new ConcurrentHashMap<>();

    public ParameterFilterMatcher(String format) {
        super(null);

        StringBuilder patternBuilder = new StringBuilder();

        StringBuilder variableNameBuilder = new StringBuilder();
        StringBuilder variableRegexBuilder = new StringBuilder();
        Set<String> variableNames = new HashSet<>();

        int state = 0;
        char ch;
        boolean spaceChar;
        boolean currentSpaceReplaced = false;

        for (int index = 0; index < format.length(); index++) {
            ch = format.charAt(index);
            spaceChar = Character.isSpaceChar(ch);

            switch (state) {
                case 0:
                    if (ch == '{') {
                        currentSpaceReplaced = false;
                        state = 1;
                        continue;
                    }
                    if (spaceChar) {
                        if (!currentSpaceReplaced) {
                            patternBuilder.append(SPACING);
                            currentSpaceReplaced = true;
                        }
                    } else {
                        patternBuilder.append(ch);
                        currentSpaceReplaced = false;
                    }
                    break;
                case 1:
                    // 获得变量名
                    switch (ch) {
                        case '}':
                            // 变量定义终结
                            final String variableName = variableNameBuilder.toString();
                            variableNames.add(variableName);
                            if (Objects.equals(variableName, "remain")) {
                                patternBuilder.append("(?<remain>" + REMAIN_VARIABLE_REGEX + ")");
                            } else {
                                patternBuilder.append("(?<" + variableName + ">" + NORMAL_VARIABLE_REGEX + ")");
                            }
                            variableNameBuilder.setLength(0);
                            state = 0;
                            break;
                        case ',':
                            // 正则表达式声明
                            state = 2;
                            break;
                        default:
                            variableNameBuilder.append(ch);
                    }
                    break;
                case 2:
                    // 获得变量的正则
                    switch (ch) {
                        case '}':
                            // 变量定义终结
                            final String variableName = variableNameBuilder.toString();
                            final String variableRegex = variableRegexBuilder.toString();
                            variableNames.add(variableName);

                            if (variableRegex.isEmpty()) {
                                patternBuilder.append("(?<" + variableName + ">\\S+)");
                            } else {
                                patternBuilder.append("(?<" + variableName + ">" + variableRegex + ")");
                                variableRegexBuilder.setLength(0);
                            }
                            variableNameBuilder.setLength(0);
                            state = 0;
                            break;
                        default:
                            variableRegexBuilder.append(ch);
                    }
                    break;
                default:
                    throw new XiaomingRuntimeException("在解析指令格式时出现错误的状态：" + state + "（位于指令处理器 " + getClass().getName() + "）");
            }
        }

        this.pattern = Pattern.compile(patternBuilder.toString());
        this.parameterNames = variableNames;
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        final Matcher matcher = pattern.matcher(message.serialize());
        matchers.put(user.getCode(), matcher);
        return matcher.matches();
    }

    public Matcher getMatcher(long qq) {
        final Matcher matcher = matchers.get(qq);
        matchers.remove(qq);
        return matcher;
    }
}
