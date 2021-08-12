package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.utility.StringUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.account.record.MemberCommandRecord;
import cn.chuanwise.xiaoming.account.record.PrivateCommandRecord;
import cn.chuanwise.xiaoming.annotation.Customizable;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.interactor.customizer.Customizer;
import cn.chuanwise.xiaoming.interactor.filter.FilterMatcher;
import cn.chuanwise.xiaoming.interactor.filter.ParameterFilterMatcher;
import cn.chuanwise.xiaoming.interactor.information.InteractorMethodInformation;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.permission.PermissionGroup;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.account.record.GroupCommandRecord;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.utility.AtUtility;
import cn.chuanwise.xiaoming.utility.MiraiCodeUtility;
import net.mamoe.mirai.message.data.MessageChain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 小明的上下文相关交互器
 * 指令处理器和上下文相关交互器的父类
 * @author Chuanwise
 */
public interface Interactor extends PluginObject {
    /** 初始化方法，主要是加载子交互函数之类 */
    void initialize();

    /**
     * 查看用户是否具有交互资格
     * @param user 申请发起用户
     * @return 其是否具有交互资格
     */
    default boolean willInteract(XiaomingUser user) {
        if (user instanceof ConsoleXiaomingUser) {
            return true;
        }

        final XiaomingPlugin plugin = getPlugin();

        // 检查是否屏蔽插件
        return !Objects.nonNull(plugin) || !user.hasTag("plugin.block." + plugin.getName());
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

        for (InteractorMethodInformation interactorMethodInformation : getMethodInformation().values()) {
            // 验证响应条件
            if (!interactorMethodInformation.willInteract(user)) {
                continue;
            }

            // 找一个可匹配的过滤器
            FilterMatcher filter = null;
            for (FilterMatcher filterMatcher : interactorMethodInformation.getFilterMatchers()) {
                if (filterMatcher.apply(user, message)) {
                    filter = filterMatcher;
                    break;
                }
            }

            if (Objects.isNull(filter)) {
                continue;
            }

            // 判断是否为同意使用条例
            if (!isAgreed && !interactorMethodInformation.isExternalUsable()) {
                user.sendError("{lang.pleaseAgreeLicense}");
                user.setInteractor(null);
                return false;
            }

            final Method method = interactorMethodInformation.getMethod();
            final Parameter[] parameters = method.getParameters();
            final List<Object> arguments = new ArrayList<>(parameters.length);

            // 获得截取下来的变量值
            boolean isParameterFilter = filter instanceof ParameterFilterMatcher;
            Map<String, String> argumentValues = null;
            if (isParameterFilter) {
                argumentValues = ((ParameterFilterMatcher) filter).getArgumentValues(user.getCode());
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
            final Class<? extends XiaomingContact> contactClass = user.getContact().getClass();

            for (Parameter parameter : parameters) {
                final Class<?> type = parameter.getType();
                if (type.isAssignableFrom(userClass)) {
                    arguments.add(user);
                } else if (type.isAssignableFrom(messageClass)) {
                    arguments.add(message);
                } else if (type.isAssignableFrom(contactClass)) {
                    arguments.add(user.getContact());
                } else if (FilterMatcher.class.isAssignableFrom(type)) {
                    arguments.add(filter);
                } else if (InteractorMethodInformation.class.isAssignableFrom(type)) {
                    arguments.add(interactorMethodInformation);
                } else if (isParameterFilter && parameter.isAnnotationPresent(FilterParameter.class)) {
                    // 带有 FilterParameter 注解，可能是 String 也可能不是
                    final FilterParameter filterParameter = parameter.getAnnotation(FilterParameter.class);
                    final String parameterName = filterParameter.value();
                    final String parameterValue;

                    parameterValue = argumentValues.getOrDefault(parameterName, filterParameter.defaultValue());
                    final String defaultValue = filterParameter.defaultValue();
//                    user.setProperty(parameterName, parameterValue);

                    // 如果是 String，就直接填充，否则交给 onParameter
                    if (String.class.isAssignableFrom(type)) {
                        arguments.add(parameterValue);
                    } else {
                        if (String[].class.isAssignableFrom(type) && Objects.equals("arguments", parameterName)) {
                            arguments.add(ArgumentUtility.splitArgs(message.serialize()).toArray(new String[0]));
                            continue;
                        }
                        final Object argument = parseParameter(user, new InteractorArgumentInformation<>(type, parameterName, parameterValue, defaultValue, interactorMethodInformation), argumentValues);
                        if (Objects.nonNull(argument)) {
                            arguments.add(argument);
                        } else {
                            break;
                        }
                    }
                } else {
                    final Object argument = parseParameter(user, parameter);
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
                    final String[] permissions = interactorMethodInformation.getPermissions();
                    for (String permission : permissions) {
                        String replacedPermission = ArgumentUtility.render(permission, getXiaomingBot().getConfiguration().getMaxIterateTime(), argumentValues);
                        if (!user.requirePermission(replacedPermission)) {
                            callable = false;
                            break;
                        }
                    }

                    if (!callable) {
                        user.setInteractor(null);
                        return true;
                    }

                    final Account account = user.getAccount();
                    final List<CommandRecord> commands = account.getCommands();
                    final int sizeBeforeInteract = commands.size();

                    final Object result = method.invoke(this, arguments.toArray(new Object[0]));

                    // 判断是否交互了
                    if (result instanceof Boolean) {
                        interacted = ((Boolean) result);
                    } else {
                        interacted = true;
                    }

                    // 添加指令使用记录
                    if (interacted) {
                        final CommandRecord record;
                        if (user instanceof GroupXiaomingUser) {
                            record = new GroupCommandRecord(((GroupXiaomingUser) user).getGroupCode(), message.serialize());
                        } else if (user instanceof MemberXiaomingUser) {
                            record = new MemberCommandRecord(((MemberXiaomingUser) user).getGroupCode(), message.serialize());
                        } else {
                            record = new PrivateCommandRecord(message.serialize());
                        }

                        commands.add(sizeBeforeInteract, record);
                        getXiaomingBot().getFileSaver().readyToSave(account);
                    }

                    // 增加调用统计次数
                    if (interacted) {
                        getXiaomingBot().getStatistician().increaseCallCounter();

                        // 查看是否需要阻塞
                        if (interactorMethodInformation.isNonNext()) {
                            user.setInteractor(null);
                            return true;
                        }
                    }
                } catch (InvocationTargetException exception) {
                    final Throwable cause = exception.getCause();
                    if (Objects.nonNull(cause)) {
                        if (!onThrowable(user, message, argumentValues, method, arguments, interactorMethodInformation, cause)) {
                            if (cause instanceof Exception) {
                                throw (Exception) cause;
                            } else {
                                cause.printStackTrace();
                            }
                        }
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
     * 注册一个交互方法
     * @param method 交互方法
     * @return 是否注册成功
     */
    default void register(Method method) {
        InteractorMethodInformation interactorMethodInformation = new InteractorMethodInformation(method);

        // 尝试使用自定义设置
        final Customizable[] customInformation = method.getAnnotationsByType(Customizable.class);
        if (Objects.nonNull(getCustomizer()) && customInformation.length != 0) {
            final InteractorMethodInformation savedInformation = getCustomizer().forName(customInformation[0].value());
            if (Objects.nonNull(savedInformation)) {
                interactorMethodInformation = savedInformation;
            }
        }

        register(method, interactorMethodInformation);
    }

    void setCustomizer(Customizer customizer);

    Customizer getCustomizer();

    /**
     * 注册一个指定权限和过滤器的交互方法
     * @param method 交互方法
     * @param interactorMethodInformation 交互方法格式
     */
    default void register(Method method, InteractorMethodInformation interactorMethodInformation) {
        interactorMethodInformation.setMethod(method);
        register(interactorMethodInformation);
    }

    /**
     * 注册一个指定权限和过滤器的交互方法
     * @param interactorMethodInformation 交互方法格式
     */
    default void register(InteractorMethodInformation interactorMethodInformation) {
        getMethodInformation().put(interactorMethodInformation.getName(), interactorMethodInformation);
    }

    /**
     * 解析未知的参数
     * @param user 当前用户
     * @param parameter 无法自动注入的参数
     * @return 注入结果。如果为 {@code null} 则注入失败
     */
    default Object parseParameter(XiaomingUser user, Parameter parameter) {
        return null;
    }

    /**
     * 解析未知的使用 @FilterParameter 注解的参数
     * @param user 当前用户
     * @param information 交互信息
     * @return 注入结果。如果为 {@code null} 则注入失败
     */
    default <T> T parseParameter(XiaomingUser user, InteractorArgumentInformation<T> information, Map<String, String> argumentValues) {
        Class<T> clazz = information.clazz;
        String currentValue = information.currentValue;
        String parameterName = information.parameterName;

        Object result = null;

        // 如果是 long 且为 qq
        if (long.class.isAssignableFrom(clazz) && "qq".equalsIgnoreCase(parameterName)) {
            final long qq = AtUtility.parseAt(currentValue);
            if (qq == -1) {
                user.sendError("{lang.illegalQQ}", currentValue);
                result = null;
            } else {
                result = qq;
            }
        } else if (clazz.isAssignableFrom(Account.class) && (Objects.equals("account", parameterName) || Objects.equals("qq", parameterName))) {
            final long qq = AtUtility.parseAt(currentValue);
            if (qq == -1) {
                user.sendError("{lang.illegalQQ}", currentValue);
                result = null;
            } else {
                result = getXiaomingBot().getAccountManager().forAccount(qq);
            }
        } else if (long.class.isAssignableFrom(clazz) && (Objects.equals("time", parameterName) || Objects.equals("period", parameterName))) {
            final long time = TimeUtility.parseTimeLength(currentValue);
            if (time == -1) {
                user.sendError("{lang.illegalPeriod}", currentValue);
                result = null;
            } else {
                result = time;
            }
        } else if (int.class.isAssignableFrom(clazz)) {
            if (Objects.equals(parameterName, "index")) {
                if (currentValue.matches("\\d+")) {
                    return (T) (Object) Integer.parseInt(currentValue);
                } else {
                    user.sendError("{lang.illegalIndex}", currentValue);
                }
            } else {
                if (currentValue.matches("[+-]?\\d+")) {
                    return (T) (Object) Integer.parseInt(currentValue);
                } else {
                    user.sendError("{lang.illegalInteger}", currentValue);
                }
            }
        } else if (long.class.isAssignableFrom(clazz) && Objects.equals("group", parameterName)) {
            if (currentValue.matches("\\d+")) {
                return (T) (Object) Long.parseLong(currentValue);
            } else {
                user.sendError("{lang.illegalGroupCode}", currentValue);
            }
        } else if (XiaomingPlugin.class.isAssignableFrom(clazz)) {
            final XiaomingPlugin plugin = getXiaomingBot().getPluginManager().getLoadedPlugin(currentValue);
            if (Objects.isNull(plugin)) {
                user.sendError("{lang.pluginHadNotLoad}", currentValue);
            } else {
                return ((T) plugin);
            }
        } else if (Objects.equals(parameterName, "permissionGroup") && PermissionGroup.class.isAssignableFrom(clazz)) {
            if (StringUtility.isEmpty(currentValue)) {
                user.sendMessage("{lang.queryPermissionGroupName}");
                currentValue = user.nextInput().serialize();
            }
            final PermissionGroup permissionGroup = getXiaomingBot().getPermissionManager().forPermissionGroup(currentValue);
            if (Objects.nonNull(permissionGroup)) {
                result = permissionGroup;
            } else {
                user.sendError("{lang.noSuchPermissionGroup}", currentValue);
            }
        }
        return ((T) result);
    }

    default boolean isLegalUser(XiaomingUser user) {
        return true;
    }

    default void onIllegalUser(XiaomingUser user) {}

    default boolean onThrowable(XiaomingUser user, Message message, Map<String, String> argumentValues, Method method, List<Object> finalArguments, InteractorMethodInformation interactorMethodInformation, Throwable throwable) {
        return false;
    }

    /**
     * 获得用户可用的指令格式
     * @param user 获取用户
     * @return 指令格式集合
     */
    default Set<String> getUsageStrings(XiaomingUser user) {
        Set<String> usages = new HashSet<>();
        for (InteractorMethodInformation interactorMethodInformation : getMethodInformation().values()) {
            if (user.hasPermission(interactorMethodInformation.getPermissions())) {
                usages.addAll(Arrays.asList(interactorMethodInformation.getUsages()));
            }
        }
        return usages;
    }

    /** 向用户显示指令格式 */
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
            final MessageChain messageChain = MiraiCodeUtility.asMessageChain(builder.toString());
            user.sendPrivateMessage(messageChain);
        }
    }

    Map<String, InteractorMethodInformation> getMethodInformation();

    default InteractorMethodInformation forMethodInformation(String name) {
        return getMethodInformation().get(name);
    }

    void setUsageCommandFormat(String usageCommandFormat);

    String getUsageCommandFormat();
}
