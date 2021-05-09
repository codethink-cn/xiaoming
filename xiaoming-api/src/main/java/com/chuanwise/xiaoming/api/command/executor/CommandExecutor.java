package com.chuanwise.xiaoming.api.command.executor;

import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.Getter;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CommandExecutor extends XiaomingObject {
    /**
     * 提取指令参数时的正则表达式
     */
    Pattern PARAMETER_REGEX = Pattern.compile("\\((?<fst>[^|)]+).*?\\)");

    /**
     * 重新统计指令处理方法
     * @param logger
     */
    void reloadSubcommandExecutor(Logger logger);

    /**
     * 指令头
     * @return
     */
    default String getCommandPrefix() {
        return "#";
    }

    /**
     * 和用户交互
     * @param user
     * @return
     * @throws Exception
     */
    boolean onCommand(XiaomingUser user) throws Exception;

    /**
     * 当遇到不能自动填充的参数时
     * @param user
     * @param parameterName
     * @param value
     * @return 如果仍然无法填充，返回 {@code null}
     */
    Object onParameter(XiaomingUser user, String parameterName, String value);

    /**
     * 处理没有 {@code @CommandParameter} 注解的参数
     * @param user
     * @param parameter
     * @return
     */
    default Object onParameter(XiaomingUser user, Parameter parameter) {
        return null;
    }

    /**
     * 指令格式帮助头
     * @return 如果为 ""，则不启用自动指令格式生成，否则启动。
     */
    default String usageStringsPrefix() {
        return "";
    }

    /**
     * 获得用户可用的指令格式
     * @param user 获取用户
     * @return 指令格式集合
     */
    default Set<String> getUsageStrings(XiaomingUser user) {
        Set<String> usages = new HashSet<>();
        for (ExecuteMethod executorMethod : getExecutorMethods()) {
            if (user.hasPermissions(executorMethod.getRequiredPermission())) {
                for (String usage : executorMethod.getUsages()) {
                    usages.add(getCommandPrefix() + usage);
                }
            }
        }
        return usages;
    }

    /**
     * 向用户显示指令格式
     * @param user 获取用户
     */
    default void showUsageStrings(XiaomingUser user) {
        StringBuilder builder = new StringBuilder();

       Set<String> usageStrings = getUsageStrings(user);
        if (usageStrings.isEmpty()) {
            builder.append("你没有权限执行该组任何一个指令");
        } else {
           String[] strings = usageStrings.toArray(new String[0]);
            Arrays.sort(strings);
            builder.append("该组指令中你可能有权执行的有如下 " + usageStrings.size() + " 条：");
            for (String s : strings) {
                builder.append("\n").append(s);
            }
        }

        if (user instanceof GroupXiaomingUser) {
            ((GroupXiaomingUser) user).sendPrivateMessage(builder.toString());
        } else {
            user.sendMessage(builder.toString());
        }
    }

    /**
     * 获得指令处理方法集合
     * @return
     */
    Set<ExecuteMethod> getExecutorMethods();

    default boolean isLegalUser(XiaomingUser user) {
        return true;
    }

    /**
     * 指令处理方法
     * @author Chuanwise
     */
    @Getter
    class ExecuteMethod {
        public static final String NORMAL_VARIABLE_REGEX = "\\S+";
        public static final String REMAIN_VARIABLE_REGEX = "[\\s\\S]*";

        /**
         * 指令处理方法的格式
         * @author Chuanwise
         */
        @Getter
        public static class Format {
            Pattern pattern;
            Set<String> variableNames;

            public Format(String format) {
                Pattern result;
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
                                    patternBuilder.append("\\s+");
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
                this.variableNames = variableNames;
            }
        }

        Method method;

        Format[] formats;

        String[] requiredPermission;

        String[] usages;

        public ExecuteMethod(final Method method) {
            this.method = method;
            final Command[] commands = method.getAnnotationsByType(Command.class);

            // 记录匹配方式
            List<Format> formats = new ArrayList<>();
            for (Command command : commands) {
                formats.add(new Format(command.value()));
            }
            this.formats = formats.toArray(new Format[0]);
            fillUsages();

            final RequirePermission[] permissions = method.getAnnotationsByType(RequirePermission.class);
            List<String> requiredPermissions = new ArrayList<>();
            for (RequirePermission permission : permissions) {
                requiredPermissions.add(permission.value());
            }
            this.requiredPermission = requiredPermissions.toArray(new String[0]);
        }

        void fillUsages() {
            List<String> usages = new ArrayList<>();
            for (Format format : formats) {
                final StringBuilder builder = new StringBuilder(format.pattern.pattern()
                        .replaceAll(Pattern.quote("\\s+"), "  ")
                        .replaceAll(Pattern.quote("(?"), "")
                        .replaceAll(Pattern.quote(NORMAL_VARIABLE_REGEX + ")"), "")
                        .replaceAll(Pattern.quote(REMAIN_VARIABLE_REGEX + ")"), "")
                        .replaceAll(Pattern.quote("\\[CAT:at,code="), "@")
                        .replaceAll(Pattern.quote("\\]"), ""));
                while (true) {
                    final Matcher matcher = PARAMETER_REGEX.matcher(builder);
                    if (matcher.find()) {
                        builder.replace(matcher.start(), matcher.end(), matcher.group("fst"));
                    } else {
                        break;
                    }
                }
                usages.add(builder.toString());
            }
            this.usages = usages.toArray(new String[0]);
        }

        public ExecuteMethod(final Method method,
                             final Format[] formats,
                             final String[] requiredPermission) {
            this.method = method;
            this.formats = formats;
            fillUsages();
            this.requiredPermission = requiredPermission;
        }
    }
}
