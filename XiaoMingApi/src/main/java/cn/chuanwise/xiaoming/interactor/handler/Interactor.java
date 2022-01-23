package cn.chuanwise.xiaoming.interactor.handler;

import cn.chuanwise.optional.ValueWithMessage;
import cn.chuanwise.toolkit.optional.SimpleValueWithMessage;
import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.*;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.annotation.*;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.InteractEvent;
import cn.chuanwise.xiaoming.exception.InteractInterrtuptedException;
import cn.chuanwise.xiaoming.exception.InteractExitedException;
import cn.chuanwise.xiaoming.exception.InteractTimeoutException;
import cn.chuanwise.xiaoming.interactor.InteractResult;
import cn.chuanwise.xiaoming.interactor.Interactors;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.filter.FilterMatcher;
import cn.chuanwise.xiaoming.interactor.filter.ParameterFilterMatcher;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterContext;
import cn.chuanwise.xiaoming.permission.Permission;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.user.*;
import lombok.*;
import net.mamoe.mirai.message.code.MiraiCode;

import java.beans.Transient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * 描述指令的格式。
 *
 */
@Data
@NoArgsConstructor
public class Interactor {
    String name;
    String[] formats = ArrayUtil.emptyArray(String.class);
    Permission[] permissions = ArrayUtil.emptyArray(Permission.class);
    String usage = null;
    String[] requireGroupTags = ArrayUtil.emptyArray(String.class);
    String[] requireAccountTags = ArrayUtil.emptyArray(String.class);

    boolean nonNext = false;

    /** 具体的交互方法 */
    transient Method method;
    transient Interactors interactors;
    transient Plugin plugin;

    @Transient
    public Method getMethod() {
        return method;
    }

    /** 交互判定器 */
    transient FilterMatcher[] filterMatchers;

    @Transient
    public FilterMatcher[] getFilterMatchers() {
        return filterMatchers;
    }

    /** 从交互方法获得指令格式。如果该方法不是交互方法，抛出异常 */
    public Interactor(Method method, Plugin plugin) {
        this.plugin = plugin;
        this.method = method;

        // 设置所需权限
        final Filter[] filters = method.getAnnotationsByType(Filter.class);
        if (filters.length == 0) {
            throw new IllegalArgumentException("method: " + method + " is not a interact method.");
        }

        // 获取交互器名
        final Name[] names = method.getAnnotationsByType(Name.class);
        if (names.length != 0) {
            name = names[0].value();
        } else {
            name = method.getName();
        }

        // 获得指令格式
        formats = Stream.of(filters)
                .filter(filter -> filter.pattern() == FilterPattern.PARAMETER)
                .map(Filter::value)
                .collect(Collectors.toList())
                .toArray(new String[0]);
        // 编译参数解析器
        filterMatchers = ArrayUtil.copyAs(filters, FilterMatcher.class, FilterMatcher::filterMatcher);

        // 获得相关权限
        final Required[] permissionAnnotations = method.getAnnotationsByType(Required.class);
        if (permissionAnnotations.length != 0) {
            permissions = ArrayUtil.copyAs(permissionAnnotations, Permission.class, x -> Permission.compile(x.value()));
        }

        // 获得指令用法
        final Usage[] usages = method.getAnnotationsByType(Usage.class);
        final ParameterFilterMatcher firstParameterFilterMatcher = (ParameterFilterMatcher) CollectionUtil.first(Arrays.asList(filterMatchers), matcher -> (matcher instanceof ParameterFilterMatcher));
        if (usages.length != 0) {
            usage = usages[0].value();
        } else if (Objects.nonNull(firstParameterFilterMatcher)) {
            usage = firstParameterFilterMatcher.toUsage();
        }

        requireAccountTags = ArrayUtil.copyAs(method.getAnnotationsByType(RequireAccountTag.class), String.class, RequireAccountTag::value);
        requireGroupTags = ArrayUtil.copyAs(method.getAnnotationsByType(RequireGroupTag.class), String.class, RequireGroupTag::value);
    }

    public Interactor(Plugin plugin,
                      String name,
                      String[] formats,
                      Permission[] permissions,
                      String[] requireGroupTags,
                      String[] requireAccountTags,
                      boolean nonNext) {
        this.plugin = plugin;
        this.name = name;
        setFormats(formats);
        this.permissions = permissions;
        this.requireAccountTags = requireAccountTags;
        this.requireGroupTags = requireGroupTags;
        this.nonNext = nonNext;
    }

    public void setFormats(String[] formats) {
        this.formats = formats;
        this.filterMatchers = ArrayUtil.copyAs(formats, FilterMatcher.class, FilterMatcher::parameter);
    }

    /** 和用户交互 */
    public ValueWithMessage<InteractResult> interact(XiaomingUser user, Message message) {
        // 如果在群里，检查该用户是否屏蔽了本插件、群里是否屏蔽了等等
        final boolean inGroup = user instanceof GroupXiaomingUser;
        final Account account = user.getAccount();
        final XiaomingBot xiaomingBot = user.getXiaomingBot();
        final XiaomingContact contact = user.getContact();

        if (inGroup) {
            final GroupContact groupContact = (GroupContact) contact;

            // 检查本群的标记是否齐备
            for (String tag : requireGroupTags) {
                if (!contact.hasTag(tag)) {
                    return new SimpleValueWithMessage<>(InteractResult.LACK_GROUP_TAGS, tag);
                }
            }
            user.setProperty(PropertyType.GROUP, groupContact);
        } else {
            user.removeProperty(PropertyType.GROUP);
        }
        user.removeProperty(PropertyType.ARGUMENTS);

        // 检查账户标记
        for (String tag : requireGroupTags) {
            if (!account.hasTag(tag)) {
                return new SimpleValueWithMessage<>(InteractResult.LACK_ACCOUNT_TAGS, tag);
            }
        }

        // 检查消息格式
        final String serializedMessage = message.serialize();
        Map<String, String> arguments = null;
        FilterMatcher matchedFilterMatcher = null;

        for (FilterMatcher filterMatcher : filterMatchers) {
            if (filterMatcher instanceof ParameterFilterMatcher) {
                final Optional<Map<String, String>> optionalParseResult = ((ParameterFilterMatcher) filterMatcher).parse(serializedMessage);
                if (optionalParseResult.isPresent()) {
                    arguments = optionalParseResult.get();
                    matchedFilterMatcher = filterMatcher;
                    break;
                }
            } else if (filterMatcher.apply(user, message)) {
                matchedFilterMatcher = filterMatcher;
                break;
            }
        }

        // 检查是否有格式匹配上了
        if (Objects.isNull(matchedFilterMatcher)) {
            return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_FORMAT);
        }

        // 解析参数
        final Class<? extends XiaomingUser> userClass = user.getClass();
        final Class<? extends XiaomingContact> contactClass = contact.getClass();
        final Parameter[] parameters = method.getParameters();
        final List<Object> methodArguments = new ArrayList<>(parameters.length);
        final Map<String, Object> argumentValues = new HashMap<>();

        // 交互器上下文
        final InteractorContext interactorContext = new InteractorContext(user, this, plugin, message, arguments, argumentValues, methodArguments);
        try {
            user.setInteractorContext(interactorContext);
            // 替换所有的权限变量，检查权限
            for (Permission permission : permissions) {
                if (!user.hasPermission(permission)) {
                    return new SimpleValueWithMessage<>(InteractResult.LACK_PERMISSIONS, permission.toString());
                }
            }

            // 准备交互
            final InteractEvent interactEvent = new InteractEvent(interactorContext);
            xiaomingBot.getEventManager().callEvent(interactEvent);
            if (interactEvent.isCancelled()) {
                return new SimpleValueWithMessage<>(InteractResult.EVENT_CANCELLED);
            } else {
                xiaomingBot.getStatistician().increaseEffectiveCallNumber();
            }

            // 填充参数
            for (Parameter parameter : parameters) {
                final Class<?> type = parameter.getType();
                if (XiaomingUser.class.isAssignableFrom(type)) {
                    if (type.isAssignableFrom(userClass)) {
                        methodArguments.add(user);
                    } else {
                        return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_SCOPE);
                    }
                } else if (XiaomingContact.class.isAssignableFrom(type)) {
                    if (type.isAssignableFrom(contactClass)) {
                        methodArguments.add(contact);
                    } else {
                        return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_SCOPE);
                    }
                } else if (Message.class.isAssignableFrom(type)) {
                    if (type.isAssignableFrom(message.getClass())) {
                        methodArguments.add(message);
                    } else {
                        return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_SCOPE);
                    }
                } else if (type.isAssignableFrom(Interactor.class)) {
                    methodArguments.add(this);
                } else if (type.isAssignableFrom(FilterMatcher.class)) {
                    methodArguments.add(matchedFilterMatcher);
                } else if (type.isAssignableFrom(ParameterFilterMatcher.class) && Objects.nonNull(arguments)) {
                    methodArguments.add(matchedFilterMatcher);
                } else if (parameter.isAnnotationPresent(FilterParameter.class)) {
                    // 如果是带有 FilterParameter 的，可能是普通的匹配器，也可能是变量匹配器
                    Container<?> container = null;
                    final FilterParameter filterParameter = parameter.getAnnotation(FilterParameter.class);
                    final String defaultValue = filterParameter.defaultValue();
                    final String parameterName = filterParameter.value();
                    final String currentValue;

                    if (Objects.nonNull(arguments)) {
                        // 变量匹配器
                        currentValue = arguments.getOrDefault(parameterName, defaultValue);
                        if (type.isAssignableFrom(String.class)) {
                            container = Container.of(currentValue);
                        }
                    } else {
                        // 普通的匹配器
                        currentValue = "";
                    }

                    // 启动匹配器
                    if (Objects.isNull(container)) {
                        final InteractorParameterContext context =
                                new InteractorParameterContext<>(user, this, plugin,
                                        arguments, methodArguments, argumentValues, type, message, parameterName, currentValue, defaultValue);
                        container = xiaomingBot.getInteractorManager().parseParameter(context);
                    }

                    // 启动变量计算
                    if (Objects.isNull(container)) {
                        final Object object;
                        final int dotPosition = parameterName.indexOf(".");
                        final boolean noDot = dotPosition == -1;

                        final Object baseVariable = xiaomingBot.getLanguageManager()
                                .getGlobalVariable(noDot ? parameterName : parameterName.substring(dotPosition + 1));
                        if (noDot) {
                            object = baseVariable;
                        } else {
                            object = xiaomingBot.getLanguageManager().caculate(baseVariable, parameterName.substring(dotPosition + 1));
                        }

                        if (Objects.nonNull(object)) {
                            container = Container.of(object);
                        }
                    }

                    // 检查匹配结果
                    if (Objects.isNull(container) || !container.isPresent()) {
                        return new SimpleValueWithMessage<>(InteractResult.PARSE_FAILED, parameterName);
                    }
                    final Object argument = container.get();
                    if (Objects.nonNull(argument)) {
                        final Class<?> argumentClass = argument.getClass();

                        // 参数类型错误
                        if (!NumberUtil.isCorresponding(argumentClass, type) && !type.isAssignableFrom(argumentClass)) {
                            return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_TYPE, parameterName);
                        }
                    }

                    argumentValues.put(parameterName, argument);
                    methodArguments.add(argument);
                } else {
                    return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_PARAMETER, type.getSimpleName());
                }
            }

            // 调用函数
            try {
                method.setAccessible(true);

                final Object invokeResult = method.invoke(interactors, methodArguments.toArray(new Object[0]));
                final boolean interacted;
                if (invokeResult instanceof Boolean) {
                    interacted = (Boolean) invokeResult;
                } else {
                    interacted = true;
                }

                if (interacted) {
                    return new SimpleValueWithMessage<>(InteractResult.INTERACTED);
                } else {
                    return new SimpleValueWithMessage<>(InteractResult.INTERACTED_BUT_FALSE);
                }
            } catch (IllegalAccessException ignored) {
                return null;
            } catch (Throwable throwable) {
                if (throwable instanceof InvocationTargetException && Objects.nonNull(throwable.getCause())) {
                    throwable = throwable.getCause();
                }

                // 如果是人为取消的，则不使用异常捕捉器
                if (throwable instanceof InteractExitedException) {
                    user.sendMessage("退出成功");
                    return new SimpleValueWithMessage<>(InteractResult.EXIT);
                } else if (throwable instanceof InteractInterrtuptedException) {
                    user.sendMessage("交互被取消");
                    return new SimpleValueWithMessage<>(InteractResult.INTERRUPTED);
                } else if (throwable instanceof InteractTimeoutException) {
                    final long timeout = ((InteractTimeoutException) throwable).getTimeout();
                    user.sendError("你已经「" + TimeUtil.toTimeLength(timeout) + "」没有回复小明啦，小明就不等待你的下一条消息啦");
                    return new SimpleValueWithMessage<>(InteractResult.TIMEOUT_CANCELLED);
                }

                xiaomingBot.getInteractorManager().onThrowable(interactorContext, throwable);
                return new SimpleValueWithMessage<>(InteractResult.ERROR);
            }
        } finally {
            user.setInteractorContext(null);
        }
    }
}