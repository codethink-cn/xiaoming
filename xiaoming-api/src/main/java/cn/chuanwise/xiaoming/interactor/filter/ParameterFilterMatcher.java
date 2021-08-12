package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.utility.UsageStringUtility;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ParameterFilterMatcher extends RegexFilterMatcher {
    Set<String> parameterNames;
    Map<Long, Map<String, String>> allArgumentValues = new ConcurrentHashMap<>();

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
                // 最开始的状态，可能迁移到：
                // 普通正文状态
                // 空格字符
                // 变量定义头：{
                case 0: {
                    if (ch == '{') {
                        currentSpaceReplaced = false;
                        state = 1;
                        continue;
                    }
                    if (spaceChar) {
                        if (!currentSpaceReplaced) {
                            patternBuilder.append(UsageStringUtility.SPACING);
                            currentSpaceReplaced = true;
                        }
                    } else {
                        patternBuilder.append(ch);
                        currentSpaceReplaced = false;
                    }
                    break;
                }

                // 变量定义状态
                // 可能遇到：普通字符串（变量定义内容）、逗号（正则表达式分割）、}：变量定义尾
                case 1: {
                    switch (ch) {
                        case '}':
                            // 变量定义终结
                            final String variableName = variableNameBuilder.toString();
                            variableNames.add(variableName);
                            switch (variableName) {
                                case "remain":
                                    patternBuilder.append("(?<remain>" + UsageStringUtility.REMAIN_VARIABLE_REGEX + ")");
                                    break;
                                case "nullableRemain":
                                    patternBuilder.append("(?<nullableRemain>" + UsageStringUtility.NULLABLE_REMAIN_VARIABLE_REGEX + ")");
                                    break;
                                default:
                                    patternBuilder.append("(?<" + variableName + ">" + UsageStringUtility.NORMAL_VARIABLE_REGEX + ")");
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
                }

                // 变量定义中的正则表达式状态
                // 可能遇到：普通内容、}：变量定义尾
                case 2: {
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
                }
                default:
                    throw new XiaomingRuntimeException("在解析指令格式时出现错误的状态：" + state + "（位于指令处理器 " + getClass().getName() + "）");
            }
        }

        this.pattern = Pattern.compile(patternBuilder.toString());
        this.parameterNames = variableNames;
    }

    @Override
    public boolean apply(XiaomingUser user, Message message) {
        final Map<String, String> argumentValues = getArgumentValues(message.serialize());
        if (Objects.isNull(argumentValues)) {
            return false;
        }

        allArgumentValues.put(user.getCode(), argumentValues);
        return true;
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    public Map<String, String> getArgumentValues(String input) {
        final Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return null;
        }

        final Map<String, String> argumentValues = new HashMap<>();
        for (String parameterName : parameterNames) {
            argumentValues.put(parameterName, matcher.group(parameterName));
        }

        return argumentValues;
    }

    public Map<String, String> getArgumentValues(long qq) {
        final Map<String, String> argumentValue = allArgumentValues.get(qq);
        allArgumentValues.remove(qq);
        return argumentValue;
    }
}
