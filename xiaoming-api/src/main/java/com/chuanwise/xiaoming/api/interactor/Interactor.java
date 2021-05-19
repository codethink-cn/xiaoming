package com.chuanwise.xiaoming.api.interactor;

import com.chuanwise.xiaoming.api.annotation.*;
import com.chuanwise.xiaoming.api.event.InteractorResponseEvent;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.interactor.filter.FilterMatcher;
import com.chuanwise.xiaoming.api.interactor.filter.ParameterFilterMatcher;
import com.chuanwise.xiaoming.api.object.PluginObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.ArrayUtils;
import com.chuanwise.xiaoming.api.util.AtUtil;

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
    /**
     * 初始化方法，主要是加载子交互函数之类
     */
    void initialize();

    boolean isExternalUse();

    void setExternalUse(boolean externalUse);

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
        }

        final Class<? extends Interactor> clazz = getClass();

        final GroupInteractor[] groupInteractors = clazz.getAnnotationsByType(GroupInteractor.class);
        final TempInteractor[] tempInteractors = clazz.getAnnotationsByType(TempInteractor.class);
        final PrivateInteractor[] privateInteractors = clazz.getAnnotationsByType(PrivateInteractor.class);

        final boolean hasGroupRestrict = groupInteractors.length > 0;
        final boolean hasTempRestrict = tempInteractors.length > 0;
        final boolean hasPrivateRestrict = privateInteractors.length > 0;

        // 如果三个注解都没有，就通过
        if (!hasGroupRestrict && !hasTempRestrict && !hasPrivateRestrict) {
            return true;
        }

        // 群交互验证
        boolean groupVerify = false;
        if (hasGroupRestrict) {
            if (user.inGroup()) {
                final GroupInteractor annotation = groupInteractors[0];
                final long group = annotation.value();
                final long qq = annotation.qq();
                groupVerify = (group == 0 || user.getGroup().getId() == group) && (qq == 0 || user.getQQ() == qq);
            } else {
                groupVerify = false;
            }
        }

        // 临时会话验证
        boolean tempVerify = false;
        if (hasTempRestrict) {
            if (user.inTemp()) {
                final TempInteractor annotation = tempInteractors[0];
                final long group = annotation.value();
                final long qq = annotation.qq();
                tempVerify = (group == 0 || user.getGroup().getId() == group) && (qq == 0 || user.getQQ() == qq);
            } else {
                tempVerify = false;
            }
        }

        // 私聊会话验证
        boolean privateVerify = false;
        if (hasPrivateRestrict) {
            if (user.inPrivate()) {
                final PrivateInteractor annotation = privateInteractors[0];
                final long qq = annotation.value();
                privateVerify = qq == 0 || user.getQQ() == qq;
            } else {
                privateVerify = false;
            }
        }

        // 如果什么都没有打，默认全区域的交互器
        return groupVerify || tempVerify || privateVerify;
    }

    /**
     * 和一个用户交互。本方法不检查用户是否能与该交互器交互
     * @param user 当前交互用户
     * @return 是否交互成功
     * @throws Exception 交互途中抛出的异常
     */
    default boolean interact(XiaomingUser user) throws Exception {
        boolean interacted = false;
        boolean isAgreed = isExternalUse() || (!getXiaomingBot().getConfiguration().isEnableLicense() || getXiaomingBot().getLicenseManager().isAgreed(user.getQQ()));
        boolean isLegalUser = isLegalUser(user);

        for (InteractorMethodDetail detail : getMethodDetails()) {
            // 验证响应条件
            if (!detail.willInteract(user)) {
                continue;
            }

            // 得到匹配当前输入的一个过滤器
            final FilterMatcher filter = detail.getMatchableFilter(user);
            if (Objects.isNull(filter)) {
                continue;
            }

            // 判断是否为合法用户
            if (!isAgreed) {
                user.sendError("你还没有同意《小明使用须知》，告诉小明「使用小明」以开始使用小明吧");
                return false;
            }
            if (!user.requirePermission(detail.getRequiredPermissions())) {
                continue;
            }
            if (!isLegalUser) {
                onIllegalUser(user);
                return true;
            }

            final Method method = detail.getMethod();
            final Parameter[] parameters = method.getParameters();
            List<Object> arguments = new ArrayList<>(parameters.length);
            final Class<? extends XiaomingUser> userClass = user.getClass();

            boolean isParameterFilter = filter instanceof ParameterFilterMatcher;
            Matcher matcher = null;
            Set<String> parameterNames = null;
            if (isParameterFilter) {
                matcher = ((ParameterFilterMatcher) filter).getMatcher(user.getQQ());
                parameterNames = ((ParameterFilterMatcher) filter).getParameterNames();
            }

            for (Parameter parameter : parameters) {
                final Class<?> type = parameter.getType();
                if (type.isAssignableFrom(userClass)) {
                    arguments.add(user);
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
                    final String parameterValue = matcher.group(parameterName);
                    final String defaultValue = filterParameter.defaultValue();

                    // 如果是 String，就直接填充，否则交给 onParameter
                    if (String.class.isAssignableFrom(type)) {
                        if (parameterNames.contains(parameterName)) {
                            arguments.add(parameterValue);
                        } else {
                            arguments.add(defaultValue);
                        }
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

            if (arguments.size() == parameters.length) {
                // 增加调用统计次数
                getXiaomingBot().getStatistician().increaseCallCounter();
                interacted = true;

                try {
                    method.invoke(this, arguments.toArray(new Object[0]));
                    getXiaomingBot().getEventManager().callLater(new InteractorResponseEvent(this, detail, user));
                } catch (InvocationTargetException exception) {
                    final Throwable cause = exception.getCause();
                    if (Objects.nonNull(cause) && cause instanceof Exception) {
                        throw (Exception) cause;
                    } else {
                        exception.printStackTrace();
                    }
                }
                if (detail.isBlocking()) {
                    return true;
                }
            }
        }
        return interacted;
    }

    /**
     * 注册一个响应方法
     * @param method 响应方法
     * @return 是否注册成功
     */
    default boolean register(Method method) {
        final Filter[] formats = method.getAnnotationsByType(Filter.class);
        if (formats.length == 0) {
            return false;
        }

        // 复制一份 String[] 类型的指令格式
        register(method,
                ArrayUtils.copyAs(method.getAnnotationsByType(RequirePermission.class), String.class, RequirePermission::value),
                ArrayUtils.copyAs(formats, FilterMatcher.class, FilterMatcher::filterMatcher));
        return true;
    }

    /**
     * 注册一个指定权限和过滤器的响应函数
     * @param method 响应函数
     * @param requirePermissions 所需权限
     * @param matchers 过滤器
     */
    default void register(Method method, String[] requirePermissions, FilterMatcher[] matchers) {
        // 阻断式响应
        final Blocking[] blockings = method.getAnnotationsByType(Blocking.class);
        boolean isBlocking = false;
        if (blockings.length > 0) {
            isBlocking = blockings[0].value();
        }

        getMethodDetails().add(new InteractorMethodDetail(method, requirePermissions, matchers, isBlocking));
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
        if ("qq".equalsIgnoreCase(parameterName)) {
            long qq = AtUtil.parseQQ(currentValue);
            if (qq == -1) {
                user.sendError("{}不是一个合理的QQ哦", currentValue);
                return null;
            } else {
                return qq;
            }
        }
        return null;
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
                for (String usage : detail.getUsages()) {
                    usages.add(usage);
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
    }

    /**
     * 获得指令处理方法集合
     * @return
     */
    Set<InteractorMethodDetail> getMethodDetails();
}
