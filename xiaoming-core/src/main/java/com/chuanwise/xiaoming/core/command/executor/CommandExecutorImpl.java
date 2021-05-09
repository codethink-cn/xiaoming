package com.chuanwise.xiaoming.core.command.executor;

import com.chuanwise.xiaoming.api.annotation.Command;
import com.chuanwise.xiaoming.api.annotation.CommandParameter;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.command.executor.CommandExecutor;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.AtUtil;
import com.chuanwise.xiaoming.api.util.CommandWords;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Getter;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @see CommandExecutor
 */
@Getter
public class CommandExecutorImpl extends XiaomingObjectImpl implements CommandExecutor {
    /**
     * 指令处理方法集
     */
    Set<ExecuteMethod> executorMethods = new HashSet<>();

    @Override
    public void reloadSubcommandExecutor(Logger log) {
        executorMethods.clear();
        String helpPrefix = usageStringsPrefix();
        // 如果帮助指令头非空，则说明启用帮助。此时纳入帮助指令
        if (!helpPrefix.isEmpty()) {
            ExecuteMethod.Format[] formats = new ExecuteMethod.Format[1];
            formats[0] = new ExecuteMethod.Format(helpPrefix + " " + CommandWords.HELP_REGEX);
            try {
                ExecuteMethod helpExecutor = new ExecuteMethod(getClass().getMethod("showUsageStrings", XiaomingUser.class),
                        formats,
                        new String[0]);
                executorMethods.add(helpExecutor);
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }

        // 对所有的指令，检查是否有 Command 注解。如果有，则将其作为指令处理方法
        for (Method method : getClass().getMethods()) {
            Command[] commands = method.getAnnotationsByType(Command.class);
            if (commands.length != 0) {
                try {
                    executorMethods.add(new ExecuteMethod(method));
                } catch (Exception exception) {
                    log.error("方法 {} 不能作为子指令处理方法，因为解析时出现异常：{}", method, exception);
                    exception.printStackTrace();
                }
            }
        }
        if (executorMethods.isEmpty()) {
            log.warn("没有从加载任何子指令处理方法");
        } else {
            log.info("成功加载了 {} 个子指令处理方法", executorMethods.size());
        }
    }

    @Override
    public boolean onCommand(XiaomingUser user) throws Exception {
        if (!isLegalUser(user)) {
            return false;
        }

        String input = user.getMessage().trim();
        String commandPrefix = getCommandPrefix();
        if (input.startsWith(commandPrefix) && input.length() > commandPrefix.length()) {
            input = input.substring(commandPrefix.length()).trim();
        } else {
            return false;
        }
        for (ExecuteMethod executorMethod : executorMethods) {
            ExecuteMethod.Format matchableFormat = null;
            Matcher matchableMatcher = null;

            for (ExecuteMethod.Format format : executorMethod.getFormats()) {
                matchableMatcher = format.getPattern().matcher(input);
                if (matchableMatcher.matches()) {
                    matchableFormat = format;
                    break;
                }
            }

            // 匹配不成功
            if (Objects.isNull(matchableFormat)) {
                continue;
            }

            Method method = executorMethod.getMethod();

            // 验证是否具有权限
            for (RequirePermission requiredPermission : method.getAnnotationsByType(RequirePermission.class)) {
                if (!user.checkPermissionAndReport(requiredPermission.value())) {
                    return true;
                }
            }

            List<Object> arguments = new ArrayList<>();

            // 填充处理方法参数
            for (Parameter parameter : method.getParameters()) {
                Class<?> type = parameter.getType();
                // 如果是带有注解的
                if (parameter.isAnnotationPresent(CommandParameter.class)) {
                    CommandParameter commandParameter = parameter.getAnnotation(CommandParameter.class);
                    String paraName = commandParameter.value();
                    String paraValue;
                    if (matchableFormat.getVariableNames().contains(paraName)) {
                        paraValue = matchableMatcher.group(paraName);
                    } else {
                        paraValue = commandParameter.defaultValue();
                    }

                    if (type.equals(String.class)) {
                        // String 类型参数
                        arguments.add(paraValue);
                    } else {
                        Object o = onParameter(user, paraName, paraValue);
                        if (Objects.nonNull(o)) {
                            arguments.add(o);
                        } else {
                            break;
                        }
                    }
                } else if (GroupXiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof GroupXiaomingUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else if (PrivateXiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof PrivateXiaomingUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else if (XiaomingUser.class.isAssignableFrom(type)) {
                    if (user instanceof XiaomingUser) {
                        arguments.add(user);
                    } else {
                        break;
                    }
                } else {
                    Object currentParameter = onParameter(user, parameter);
                    if (Objects.nonNull(currentParameter)) {
                        arguments.add(currentParameter);
                    } else {
                        throw new IllegalArgumentException("错误的参数：" + parameter);
                    }
                }
            }

            if (arguments.size() == method.getParameterCount()) {
                method.invoke(this, arguments.toArray(new Object[0]));
                return true;
            }
        }
        return false;
    }

    @Override
    public Object onParameter(XiaomingUser user, String parameterName, String value) {
        if ("qq".equalsIgnoreCase(parameterName)) {
            String qqString = value;
            long qq = AtUtil.parseQQ(qqString);
            if (qq == -1) {
                user.sendError("{}不是一个合理的QQ哦", qqString);
                return null;
            } else {
                return qq;
            }
        }
        return null;
    }
}