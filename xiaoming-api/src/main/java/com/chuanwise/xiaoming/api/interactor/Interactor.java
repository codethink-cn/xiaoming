package com.chuanwise.xiaoming.api.interactor;

import com.chuanwise.xiaoming.api.annotation.*;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.event.InteractorResponseEvent;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import com.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;
import com.chuanwise.xiaoming.api.object.PluginObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.AtUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 小明的上下文相关交互器
 * 指令处理器和上下文相关交互器的父类
 * @author Chuanwise
 */
public interface Interactor extends PluginObject {
    String getName();

    void setName(String name);

    /**
     * 初始化方法，主要是加载子交互函数之类
     */
    void initialize();

    default boolean isLegalUser(XiaomingUser user) {
        return true;
    }

    default void onIllegalUser(XiaomingUser user) {}

    /**
     * 查看用户是否具有交互资格
     * @param user 申请发起用户
     * @return 其是否具有交互资格
     */
    default boolean willInteract(XiaomingUser user) {
        // 无条件服务控制台使用者
        if (user == getXiaomingBot().getConsoleXiaomingUser()) {
            return true;
        }
        final XiaomingPlugin plugin = getPlugin();

        // 检查是否屏蔽插件
        if (Objects.nonNull(plugin) && user.isBlockPlugin(plugin.getName())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 和一个用户交互。本方法不检查用户是否能与该交互器交互
     * @param user 当前交互用户
     * @return 是否交互成功
     * @throws Exception 交互途中抛出的异常
     */
    default boolean interact(XiaomingUser user, Message message) throws Exception {
        boolean interacted = false;
        boolean isAgreed = !getXiaomingBot().getConfiguration().isEnableLicense() || getXiaomingBot().getLicenseManager().isAgreed(user.getCode());
        boolean isLegalUser = isLegalUser(user);

        user.setInteractor(this);

        for (InteractorMethodDetail detail : getMethodDetails()) {
            // 验证响应条件
            if (!detail.willInteract(user)) {
                continue;
            }

            // 得到匹配当前输入的一个过滤器
            final FilterMatcher filter = detail.getMatchableFilter(user, message);
            if (Objects.isNull(filter)) {
                continue;
            }

            // 判断是否为合法用户
            if (!isAgreed && !detail.isExternalUsable()) {
                user.sendError("{pleaseAgreeLicense}");
                user.setInteractor(null);
                return false;
            }

            final Method method = detail.getMethod();
            final Parameter[] parameters = method.getParameters();
            List<Object> arguments = new ArrayList<>(parameters.length);

            // 准备验证权限
            boolean isParameterFilter = filter instanceof ParameterFilterMatcher;
            Matcher matcher = null;
            Set<String> parameterNames = null;
            if (isParameterFilter) {
                matcher = ((ParameterFilterMatcher) filter).getMatcher(user.getCode());
                parameterNames = ((ParameterFilterMatcher) filter).getParameterNames();
            }

            // 判断合法用户
            if (!isLegalUser) {
                onIllegalUser(user);
                user.setInteractor(null);
                return true;
            }

            // 填充参数
            final Class<? extends XiaomingUser> userClass = user.getClass();
            final Class<? extends Message> messageClass = message.getClass();
            for (Parameter parameter : parameters) {
                final Class<?> type = parameter.getType();
                if (type.isAssignableFrom(userClass)) {
                    arguments.add(user);
                } else if (type.isAssignableFrom(messageClass)) {
                    arguments.add(message);
                } else if (isParameterFilter && Matcher.class.isAssignableFrom(type)) {
                    arguments.add(matcher);
                } else if (FilterMatcher.class.isAssignableFrom(type)) {
                    arguments.add(filter);
                } else if (InteractorMethodDetail.class.isAssignableFrom(type)) {
                    arguments.add(detail);
                } else if (isParameterFilter && parameter.isAnnotationPresent(FilterParameter.class)) {
                    // 带有 FilterParameter 注解，可能是 String 也可能不是
                    final FilterParameter filterParameter = parameter.getAnnotation(FilterParameter.class);
                    final String parameterName = filterParameter.value();
                    final String parameterValue;
                    if (parameterNames.contains(parameterName)) {
                        parameterValue = matcher.group(parameterName);
                    } else {
                        parameterValue = filterParameter.defaultValue();
                    }
                    final String defaultValue = filterParameter.defaultValue();
                    user.setProperty(parameterName, parameterValue);

                    // 如果是 String，就直接填充，否则交给 onParameter
                    if (String.class.isAssignableFrom(type)) {
                        arguments.add(parameterValue);
                    } else {
                        final Object argument = onParameter(user, type, parameterName, parameterValue, defaultValue);
                        if (Objects.nonNull(argument)) {
                            arguments.add(argument);
                        } else {
                            break;
                        }
                    }
                } else {
                    final Object argument = onParameter(user, parameter);
                    if (Objects.nonNull(argument)) {
                        arguments.add(argument);
                    } else {
                        break;
                    }
                }
            }

            // 如果可以调用
            if (arguments.size() == parameters.length) {
                try {
                    boolean callable = true;

                    // 验证权限
                    final String[] permissions = detail.getRequiredPermissions();
                    for (String permission : permissions) {
                        String replacedPermission = user.replaceArguments(permission);
                        if (!user.requirePermission(replacedPermission)) {
                            callable = false;
                            break;
                        }
                    }

                    if (!callable) {
                        user.setInteractor(null);
                        return true;
                    }

                    final Object result = method.invoke(this, arguments.toArray(new Object[0]));

                    // 判断是否交互了
                    if (result instanceof Boolean) {
                        interacted = ((Boolean) result);
                    } else if (result instanceof Number) {
                        interacted = ((Number) result).longValue() > 0;
                    } else {
                        interacted = true;
                    }

                    // 增加调用统计次数
                    if (interacted) {
                        getXiaomingBot().getStatistician().increaseCallCounter();
                        getXiaomingBot().getEventManager().callLater(new InteractorResponseEvent(this, detail, user));

                        // 查看是否需要阻塞
                        if (detail.isNonNext()) {
                            user.setInteractor(null);
                            return true;
                        }
                    }
                } catch (InvocationTargetException exception) {
                    final Throwable cause = exception.getCause();
                    if (Objects.nonNull(cause) && cause instanceof Exception) {
                        throw (Exception) cause;
                    } else {
                        exception.printStackTrace();
                    }
                }
            }
        }
        user.setInteractor(null);
        return interacted;
    }

    /**
     * 注册一个响应方法
     * @param method 响应方法
     * @return 是否注册成功
     */
    default boolean register(Method method) {
        final Filter[] filters = method.getAnnotationsByType(Filter.class);
        if (filters.length == 0) {
            return false;
        }

        getMethodDetails().add(new InteractorMethodDetail(method));
        return true;
    }

    /**
     * 注册一个指定权限和过滤器的响应函数
     * @param method 响应函数
     * @param requirePermissions 所需权限
     * @param matchers 过滤器
     */
    default void register(Method method, String[] requirePermissions, FilterMatcher[] matchers, String[] usageStrings, boolean quietUsable, boolean externalUsable) {
        getMethodDetails().add(new InteractorMethodDetail(method, requirePermissions, matchers, usageStrings, method.getAnnotationsByType(NonNext.class).length > 0, quietUsable, externalUsable));
    }

    /**
     * 解析未知的参数
     * @param user 当前用户
     * @param parameter 无法自动注入的参数
     * @return 注入结果。如果为 {@code null} 则注入失败
     */
    default Object onParameter(XiaomingUser user, Parameter parameter) {
        return null;
    }

    /**
     * 解析未知的使用 @FilterParameter 注解的参数
     * @param user 当前用户
     * @param clazz 形参类型
     * @param parameterName 参数名
     * @param currentValue 当前值
     * @param defaultValue 默认值
     * @return 注入结果。如果为 {@code null} 则注入失败
     */
    default <T> Object onParameter(XiaomingUser user, Class<T> clazz, String parameterName, String currentValue, String defaultValue) {
        Object result = null;
        user.setProperty("string", currentValue);

        // 如果是 long 且为 qq
        if (long.class.isAssignableFrom(clazz) && "qq".equalsIgnoreCase(parameterName)) {
            final long qq = AtUtils.parseQQ(currentValue);
            if (qq == -1) {
                user.sendError("{stringIsNotAIllegalQQ}");
                result = null;
            } else {
                result = qq;
            }
        } else if (long.class.isAssignableFrom(clazz) && (Objects.equals("time", parameterName) || Objects.equals("period", parameterName))) {
            final long time = TimeUtils.parseTime(currentValue);
            if (time == -1) {
                user.sendError("{stringIsNotAIllegalPeriod}");
                result = null;
            } else {
                result = time;
            }
        } else if (int.class.isAssignableFrom(clazz) && Objects.equals("index", parameterName)) {
            if (currentValue.matches("\\d+")) {
                return Integer.parseInt(currentValue);
            } else {
                user.sendError("「{index}」并不是一个有效的序号哦");
            }
        }
        return result;
    }

    /**
     * 获得用户可用的指令格式
     * @param user 获取用户
     * @return 指令格式集合
     */
    default Set<String> getUsageStrings(XiaomingUser user) {
        Set<String> usages = new HashSet<>();
        for (InteractorMethodDetail detail : getMethodDetails()) {
            if (user.hasPermission(detail.getRequiredPermissions())) {
                usages.addAll(Arrays.asList(detail.getUsageStrings()));
            }
        }
        return usages;
    }

    /**
     * 向用户显示指令格式
     * @param user 获取用户
     */
    default void onUsage(XiaomingUser user) {
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
    }

    /**
     * 获得指令处理方法集合
     * @return
     */
    Set<InteractorMethodDetail> getMethodDetails();
}
