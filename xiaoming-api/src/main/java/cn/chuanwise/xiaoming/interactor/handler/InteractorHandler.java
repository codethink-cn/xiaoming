package cn.chuanwise.xiaoming.interactor.handler;

import cn.chuanwise.optional.ValueWithMessage;
import cn.chuanwise.toolkit.optional.SimpleValueWithMessage;
import cn.chuanwise.toolkit.value.container.ValueContainer;
import cn.chuanwise.utility.*;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.annotation.*;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.client.CenterClientManager;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.InteractEvent;
import cn.chuanwise.xiaoming.exception.InteractorTimeoutException;
import cn.chuanwise.xiaoming.exception.ReceptCancelledException;
import cn.chuanwise.xiaoming.interactor.InteractResult;
import cn.chuanwise.xiaoming.interactor.Interactors;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.filter.FilterMatcher;
import cn.chuanwise.xiaoming.interactor.filter.ParameterFilterMatcher;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterContext;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.attribute.AttributeType;
import cn.chuanwise.xiaoming.user.*;
import lombok.*;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

import java.beans.Transient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/***
 * 描述指令的格式。
 *
 */
@Data
@NoArgsConstructor
public class InteractorHandler {
    String name;
    String[] formats = new String[0];
    String[][] permissions = new String[0][];
    String usage = null;
    String[] requireGroupTags = new String[0];
    String[] requireAccountTags = new String[0];

    boolean externalUsable = false;
    boolean quietUsable = false;
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
    public InteractorHandler(Method method, Plugin plugin) {
        this.plugin = plugin;
        this.method = method;

        // 设置所需权限
        final Filter[] filters = method.getAnnotationsByType(Filter.class);
        if (filters.length == 0) {
            throw new IllegalArgumentException("method: " + method + " is not a interact method.");
        }

        final Name[] names = method.getAnnotationsByType(Name.class);
        if (names.length != 0) {
            name = names[0].value();
        } else {
            name = method.getName();
        }

        formats = ArrayUtility.copyAs(filters, String.class, Filter::value);
        filterMatchers = ArrayUtility.copyAs(filters, FilterMatcher.class, FilterMatcher::filterMatcher);
        permissions = ArrayUtility.copyAs(method.getAnnotationsByType(Permission.class), String[].class, Permission::value);

        final Usage[] usages = method.getAnnotationsByType(Usage.class);
        final ParameterFilterMatcher firstParameterFilterMatcher = (ParameterFilterMatcher) CollectionUtility.first(Arrays.asList(filterMatchers), matcher -> (matcher instanceof ParameterFilterMatcher));
        if (usages.length != 0) {
            usage = usages[0].value();
        } else if (Objects.nonNull(firstParameterFilterMatcher)) {
            usage = firstParameterFilterMatcher.toUsage();
        }

        quietUsable = method.getAnnotationsByType(WhenQuiet.class).length != 0;
        externalUsable = method.getAnnotationsByType(WhenExternal.class).length != 0;

        requireAccountTags = ArrayUtility.copyAs(method.getAnnotationsByType(RequireAccountTag.class), String.class, RequireAccountTag::value);
        requireGroupTags = ArrayUtility.copyAs(method.getAnnotationsByType(RequireGroupTag.class), String.class, RequireGroupTag::value);
    }

    public InteractorHandler(Plugin plugin,
                             String name,
                             String[] formats,
                             String[][] permissions,
                             String[] requireGroupTags,
                             String[] requireAccountTags,
                             boolean externalUsable,
                             boolean quietUsable,
                             boolean nonNext) {
        this.plugin = plugin;
        this.name = name;
        setFormats(formats);
        this.permissions = permissions;
        this.requireAccountTags = requireAccountTags;
        this.requireGroupTags = requireGroupTags;
        this.externalUsable = externalUsable;
        this.quietUsable = quietUsable;
        this.nonNext = nonNext;
    }

    public boolean hasPermission(XiaomingUser xiaomingUser) {
        for (String[] permission : permissions) {
            boolean hasPermission = false;
            for (String p : permission) {
                if (xiaomingUser.hasPermission(p)) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission) {
                return false;
            }
        }
        return true;
    }

    public void setFormats(String[] formats) {
        this.formats = formats;
        this.filterMatchers = ArrayUtility.copyAs(formats, FilterMatcher.class, FilterMatcher::parameter);
    }

    public String[] listPermissions() {
        final List<String> results = new ArrayList<>(permissions.length);
        for (String[] permission : permissions) {
            results.addAll(Arrays.asList(permission));
        }
        return results.toArray(new String[0]);
    }

    /** 和用户交互 */
    public <M extends Message> ValueWithMessage<InteractResult> interact(XiaomingUser<?, M, ?> user, M message) {
        // 如果在群里，检查该用户是否屏蔽了本插件、群里是否屏蔽了等等
        final boolean inGroup = user instanceof GroupXiaomingUser;
        final Account account = user.getAccount();
        final XiaomingBot xiaomingBot = user.getXiaomingBot();
        final Configuration configuration = xiaomingBot.getConfiguration();
        final XiaomingContact contact = user.getContact();

        if (inGroup) {
            final GroupContact groupContact = (GroupContact) contact;

            user.setAttribute(AttributeType.GROUP, groupContact);

            // 检查本群是否启动
            final boolean hadNotEnableYet = !externalUsable &&
                    !contact.hasTag(configuration.getEnableGroupTag());
            if (hadNotEnableYet) {
                return new SimpleValueWithMessage<>(InteractResult.NOT_ENABLE_YET);
            }

            // 检查群内是否启动了安静模式
            final boolean shouldQuiet = !quietUsable &&
                    groupContact.hasTags(configuration.getQuietModeGroupTag()) &&
                    !user.hasPermission(configuration.getQuietModeBypassPermission());
            if (shouldQuiet) {
                return new SimpleValueWithMessage<>(InteractResult.QUITE_MODE_DENIED);
            }

            // 检查是否有任何一方屏蔽了插件
            final boolean groupBlockPlugin = groupContact.getGroupRecord().isBlockPlugin(plugin);
            if (groupBlockPlugin) {
                return new SimpleValueWithMessage<>(InteractResult.GROUP_BLOCK_PLUGIN);
            }

            // 检查本群的标记是否齐备
            for (String tag : requireGroupTags) {
                if (!contact.hasTag(tag)) {
                    return new SimpleValueWithMessage<>(InteractResult.LACK_GROUP_TAGS, tag);
                }
            }
        } else {
            user.removeProperty(AttributeType.GROUP);
        }
        user.removeProperty(AttributeType.ARGUMENTS);

        // 检查用户有无屏蔽插件
        final boolean userBlockPlugin = account.isBlockPlugin(plugin);
        if (userBlockPlugin) {
            return new SimpleValueWithMessage<>(InteractResult.ACCOUNT_BLOCK_PLUGIN);
        }

        // 检查账户标记
        for (String tag : requireGroupTags) {
            if (!account.hasTag(tag)) {
                return new SimpleValueWithMessage<>(InteractResult.LACK_ACCOUNT_TAGS, tag);
            }
        }

        // 检查消息格式
        final String serializedMessage = message.serialize();
        Map<String, String> argumentValues = null;
        FilterMatcher matchedFilterMatcher = null;

        for (FilterMatcher filterMatcher : filterMatchers) {
            if (filterMatcher instanceof ParameterFilterMatcher) {
                argumentValues = ((ParameterFilterMatcher) filterMatcher).parse(serializedMessage);
                if (Objects.nonNull(argumentValues)) {
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

        // 检查该用户是否同意了小明使用协议
        final boolean isAgreedLicense = !configuration.isEnableLicense() || xiaomingBot.getLicenseManager().isAgreed(user.getCode());
        if (!isAgreedLicense) {
            return new SimpleValueWithMessage<>(InteractResult.LICENSE_DENIED);
        }

        // 解析参数
        final Class<? extends XiaomingUser> userClass = user.getClass();
        final Class<? extends XiaomingContact> contactClass = contact.getClass();
        final Parameter[] parameters = method.getParameters();
        final List<Object> arguments = new ArrayList<>(parameters.length);

        // 交互器上下文
        final InteractorContext interactorContext = new InteractorContext(user, this, plugin, message, argumentValues, arguments);
        user.setInteractorContext(interactorContext);

        // 替换所有的权限变量，检查权限
        for (String[] eachLine : permissions) {
            String noPermissionNode = null;
            for (String permission : eachLine) {
                final String checkPermission = user.format(permission);
                if (user.hasPermission(checkPermission)) {
                    noPermissionNode = null;
                    break;
                } else if (Objects.isNull(noPermissionNode)) {
                    noPermissionNode = permission;
                }
            }

            if (Objects.nonNull(noPermissionNode)) {
                return new SimpleValueWithMessage<>(InteractResult.LACK_PERMISSIONS, noPermissionNode);
            }
        }

        // 填充参数
        for (Parameter parameter : parameters) {
            final Class<?> type = parameter.getType();
            if (type.isAssignableFrom(userClass)) {
                arguments.add(user);
            } else if (type.isAssignableFrom(contactClass)) {
                arguments.add(contact);
            } else if (type.isAssignableFrom(message.getClass())) {
                arguments.add(message);
            } else if (type.isAssignableFrom(InteractorHandler.class)) {
                arguments.add(this);
            } else if (type.isAssignableFrom(FilterMatcher.class)) {
                arguments.add(matchedFilterMatcher);
            } else if (type.isAssignableFrom(ParameterFilterMatcher.class) && Objects.nonNull(argumentValues)) {
                arguments.add(matchedFilterMatcher);
            } else if (parameter.isAnnotationPresent(FilterParameter.class)) {
                // 如果是带有 FilterParameter 的，可能是普通的匹配器，也可能是变量匹配器
                ValueContainer<?> container = null;
                final FilterParameter filterParameter = parameter.getAnnotation(FilterParameter.class);
                final String defaultValue = filterParameter.defaultValue();
                final String parameterName = filterParameter.value();
                final String currentValue;

                if (Objects.nonNull(argumentValues)) {
                    // 变量匹配器
                    currentValue = argumentValues.getOrDefault(parameterName, defaultValue);
                    if (type.isAssignableFrom(String.class)) {
                        container = ValueContainer.of(currentValue);
                    }
                } else {
                    // 普通的匹配器
                    currentValue = "";
                }

                // 启动匹配器
                if (Objects.isNull(container)) {
                    final InteractorParameterContext context = new InteractorParameterContext<>(user, this, plugin, argumentValues, arguments, type, message, parameterName, currentValue, defaultValue);
                    final Object parseResult = xiaomingBot.getInteractorManager().parseParameter(context);

                    if (Objects.nonNull(parseResult)) {
                        container = ValueContainer.of(parseResult);
                    }
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
                        container = ValueContainer.of(object);
                    }
                }

                // 检查匹配结果
                if (Objects.isNull(container) || !container.hasValue()) {
                    return new SimpleValueWithMessage<>(InteractResult.PARSE_FAILED, parameterName);
                }
                final Object argument = container.getValue();
                final Class<?> argumentClass = argument.getClass();

                // 参数类型错误
                if (!ClassUtility.isCorresponding(argumentClass, type) && !type.isAssignableFrom(argumentClass)) {
                    return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_TYPE, parameterName);
                }

                arguments.add(argument);
            } else {
                return new SimpleValueWithMessage<>(InteractResult.ILLEGAL_PARAMETER);
            }
        }

        // 调用函数
        try {
            method.setAccessible(true);

            // 准备交互
            final InteractEvent interactEvent = new InteractEvent(interactorContext, user);
            xiaomingBot.getEventManager().callEvent(interactEvent);
            if (interactEvent.isCancelled()) {
                return new SimpleValueWithMessage<>(InteractResult.EVENT_CANCELLED);
            }

            final Object invokeResult = method.invoke(interactors, arguments.toArray(new Object[0]));
            final boolean interacted;
            if (invokeResult instanceof Boolean) {
                interacted = (Boolean) invokeResult;
            } else {
                interacted = true;
            }

            if (interacted) {
                // 保存调用记录
                final CommandRecord commandRecord = user.buildCommandRecord(serializedMessage);
                account.addCommand(commandRecord);
                user.getXiaomingBot().getFileSaver().readyToSave(account);

                final CenterClientManager client = user.getXiaomingBot().getCenterClientManager();
                client.doOrFail(client::increaseTotalCallNumber, "增加总小明调用次数");

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
            if (throwable instanceof ReceptCancelledException ||
                    throwable instanceof InteractorTimeoutException) {
                return new SimpleValueWithMessage<>(InteractResult.TIMEOUT_CANCELLED);
            }

            xiaomingBot.getInteractorManager().onThrowable(interactorContext, throwable);
            return new SimpleValueWithMessage<>(InteractResult.ERROR);
        } finally {
            user.setInteractorContext(null);
        }
    }

    public ValueWithMessage<InteractResult> interact(XiaomingUser user, String message) {
        return interact(user, user.buildMessage(message));
    }

    public ValueWithMessage<InteractResult> interact(XiaomingUser user, SingleMessage messages) {
        return interact(user, user.buildMessage(messages));
    }

    public ValueWithMessage<InteractResult> interact(XiaomingUser user, MessageChain messages) {
        return interact(user, user.buildMessage(messages));
    }
}