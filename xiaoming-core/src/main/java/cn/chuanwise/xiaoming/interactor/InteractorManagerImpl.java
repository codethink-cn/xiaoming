package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.optional.ValueWithMessage;
import cn.chuanwise.toolkit.value.container.ValueContainer;
import cn.chuanwise.utility.ArgumentUtility;
import cn.chuanwise.utility.NumberUtility;
import cn.chuanwise.utility.TimeUtility;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.interactor.caughter.InteractorThrowableCaughterHandler;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParserHandler;
import cn.chuanwise.xiaoming.permission.PermissionGroup;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.utility.AtUtility;
import cn.chuanwise.xiaoming.utility.RegisterUtility;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * 交互器管理器
 * @author Chuanwise
 */
@Getter
public class InteractorManagerImpl extends ModuleObjectImpl implements InteractorManager {
    protected final List<InteractorHandler> interactors = new CopyOnWriteArrayList<>();
    protected final List<InteractorParameterParserHandler> parameterParsers = new CopyOnWriteArrayList<>();
    protected final List<InteractorThrowableCaughterHandler> throwableCaughters = new CopyOnWriteArrayList<>();

    public InteractorManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
        initialize();
    }

    private void initialize() {
        registerParameterParser(XiaomingBot.class, context -> ValueContainer.of(getXiaomingBot()), true, null);
        registerParameterParser(long.class, context -> {
            final XiaomingUser user = context.getUser();
            final String inputValue = context.getInputValue();
            switch (context.getParameterName()) {
                case "qq":
                case "code":
                case "account":
                    final Long code = AtUtility.parseAt(context.getInputValue());
                    if (Objects.isNull(code)) {
                        user.sendError("{lang.illegalCode}", inputValue);
                        return null;
                    } else {
                        return ValueContainer.of(code);
                    }
                case "time":
                case "period":
                case "时长":
                    final Long time = TimeUtility.parseTimeLength(inputValue);
                    if (Objects.isNull(time)) {
                        user.sendError("{lang.illegalPeriod}", inputValue);
                        return null;
                    } else {
                        return ValueContainer.of(time);
                    }
                case "group":
                    final Long groupCode = NumberUtility.parseLong(inputValue);
                    if (Objects.isNull(groupCode)) {
                        user.sendError("{lang.illegalGroupCode}", inputValue);
                        return null;
                    } else {
                        return ValueContainer.of(groupCode);
                    }
                default:
                    final Long parseResult = NumberUtility.parseLong(inputValue);
                    if (Objects.nonNull(parseResult)) {
                        return ValueContainer.of(parseResult);
                    } else {
                        return null;
                    }
            }
        }, true, null);
        registerParameterParser(Account.class, context -> {
            final XiaomingUser user = context.getUser();
            final String inputValue = context.getInputValue();
            final long qq = AtUtility.parseAt(inputValue);
            if (qq == -1) {
                user.sendError("{lang.illegalCode}", inputValue);
                return null;
            } else {
                final Account account = getXiaomingBot().getAccountManager().getAccount(qq);
                if (Objects.nonNull(account)) {
                    return ValueContainer.of(account);
                } else {
                    return null;
                }
            }
        }, true, null);
        registerParameterParser(int.class, context -> {
            final String parameterName = context.getParameterName();
            final String inputValue = context.getInputValue();
            final XiaomingUser user = context.getUser();

            final Integer integer = NumberUtility.parseInteger(inputValue);
            if (Objects.isNull(integer)) {
                user.sendError("{lang.illegalInteger}", inputValue);
                return null;
            } else {
                switch (parameterName) {
                    case "index":
                    case "序号":
                        if (integer <= 0) {
                            user.sendError("{lang.illegalIndex}", inputValue);
                            return null;
                        } else {
                            return ValueContainer.of(integer - 1);
                        }
                    default:
                        return ValueContainer.of(integer);
                }
            }
        }, true, null);
        registerParameterParser(Plugin.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaomingUser user = context.getUser();
            final Plugin plugin = getXiaomingBot().getPluginManager().getPlugin(inputValue);

            if (Objects.isNull(plugin)) {
                user.sendError("{lang.pluginHadNotLoad}", inputValue);
                return null;
            } else {
                return ValueContainer.of(plugin);
            }
        }, true, null);
        registerParameterParser(PermissionGroup.class, context -> {
            final XiaomingUser user = context.getUser();
            final String inputValue = context.getInputValue();
            final PermissionGroup permissionGroup = getXiaomingBot().getPermissionManager().forPermissionGroup(inputValue);
            if (Objects.nonNull(permissionGroup)) {
                return ValueContainer.of(permissionGroup);
            } else {
                user.sendError("{lang.noSuchPermissionGroup}", inputValue);
                return null;
            }
        }, true, null);
        registerParameterParser(double.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaomingUser user = context.getUser();
            final Double parseResult = NumberUtility.parseDouble(inputValue);
            if (Objects.isNull(parseResult)) {
                user.sendError("{lang.illegalDouble}", inputValue);
                return null;
            } else {
                return ValueContainer.of(parseResult);
            }
        }, true, null);
        registerParameterParser(GroupRecord.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaomingUser user = context.getUser();

            final Long groupCode = NumberUtility.parseLong(inputValue);
            if (Objects.isNull(groupCode)) {
                user.sendError("{lang.illegalGroupCode}", inputValue);
                return null;
            } else {
                final GroupRecord groupRecord = getXiaomingBot().getGroupRecordManager().forCode(groupCode);
                if (Objects.isNull(groupRecord)) {
                    user.sendError("{lang.groupRecordNotFound}", inputValue);
                    return null;
                } else {
                    return ValueContainer.of(groupRecord);
                }
            }
        }, true, null);
        registerParameterParser(PluginHandler.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaomingUser user = context.getUser();
            final PluginHandler handler = getXiaomingBot().getPluginManager().getPluginHandler(inputValue);

            if (Objects.isNull(handler)) {
                user.sendError("{lang.pluginHadNotLoad}", inputValue);
                return null;
            } else {
                return ValueContainer.of(handler);
            }
        }, true, null);
        registerParameterParser(String[].class, context -> {
            switch (context.getParameterName()) {
                case "args":
                case "arguments":
                    return ValueContainer.of(ArgumentUtility.split(context.getMessage().serialize()).toArray(new String[0]));
                default:
                    return null;
            }
        }, true, null);
    }

    /** 智能参数解析器 */
    @Override
    public List<InteractorParameterParserHandler> getParameterParsers() {
        return Collections.unmodifiableList(parameterParsers);
    }

    @Override
    public <T> void registerParameterParser(InteractorParameterParserHandler<T> handler) {
        RegisterUtility.checkRegister(getXiaomingBot(), handler.getPlugin(), "parameter parser");
        parameterParsers.add(handler);
    }

    @Override
    public void unregisterParameterParsers(Plugin plugin) {
        RegisterUtility.checkUnregister(getXiaomingBot(), plugin, "parameter parser");
        parameterParsers.removeIf(parser -> (parser.getPlugin() == plugin));
    }

    /** 异常捕捉器 */
    @Override
    public List<InteractorThrowableCaughterHandler> getThrowableCaughters() {
        return Collections.unmodifiableList(throwableCaughters);
    }

    @Override
    public <T extends Throwable> void registerThrowableCaughter(InteractorThrowableCaughterHandler<T> handler) {
        RegisterUtility.checkRegister(getXiaomingBot(), handler.getPlugin(), "throwable caughter");
        throwableCaughters.add(handler);
    }

    @Override
    public void unregisterThrowableCaughters(Plugin plugin) {
        RegisterUtility.checkUnregister(getXiaomingBot(), plugin, "throwable caughter");
        throwableCaughters.removeIf(caughter -> (caughter.getPlugin() == plugin));
    }

    /** 和用户交互 */
    @Override
    public <M extends Message> boolean interactIf(XiaomingUser<?, M, ?> user, M message, Predicate<InteractorHandler> filter) {
        boolean interacted = false;

        // 如果本群没有启动小明就不管了
        final boolean hadNotEnableYet = user instanceof GroupXiaomingUser && !((GroupXiaomingUser) user).getContact().hasTag(getXiaomingBot().getConfiguration().getEnableGroupTag());
        if (hadNotEnableYet) {
            return interacted;
        }

        for (InteractorHandler interactor : interactors) {
            if (Objects.nonNull(filter) && !filter.test(interactor)) {
                continue;
            }

            boolean thisTimeInteracted = false;
            final ValueWithMessage<InteractResult> interactResult = interactor.interact(user, message);
            switch (interactResult.getValue()) {
                case ILLEGAL_TYPE:
                    user.sendError("{lang.illegalType}");
                    getLogger().error("当前用户：" + user.getAliasAndCode() + "\n" +
                            "交互器：" + interactor.getName() + "\n" +
                            "错误参数名：" + interactResult.getMessage());
                    thisTimeInteracted = true;
                    break;
                case LICENSE_DENIED:
                    user.sendMessage("{lang.pleaseAgreeLicense}");
                    thisTimeInteracted = true;
                    break;
                case INTERACTED:
                case TIMEOUT_CANCELLED:
                    thisTimeInteracted = true;
                    break;
                case EVENT_CANCELLED:
                case ILLEGAL_PARAMETER:
                case LACK_ACCOUNT_TAGS:
                case LACK_GROUP_TAGS:
                case ACCOUNT_BLOCK_PLUGIN:
                case GROUP_BLOCK_PLUGIN:
                case QUITE_MODE_DENIED:
                case NOT_ENABLE_YET:
                case PARSE_FAILED:
                case ILLEGAL_FORMAT:
                case INTERACTED_BUT_FALSE:
                    break;
                case LACK_PERMISSIONS:
                    user.sendError("{lang.lackPermission}", interactResult.getMessage());
                    thisTimeInteracted = true;
                    break;
                case ERROR:
                    thisTimeInteracted = true;
                    break;
                default:
                    throw new UnsupportedVersionException();
            }

            interacted = interacted || thisTimeInteracted;

            // 检查是否需要阻断响应
            if (thisTimeInteracted && interactor.isNonNext()) {
                return interacted;
            }
        }
        return interacted;
    }

    /** 交互器 */
    @Override
    public List<InteractorHandler> getInteractors() {
        return Collections.unmodifiableList(interactors);
    }

    @Override
    public void registerInteractor(InteractorHandler interactor) {
        RegisterUtility.checkRegister(getXiaomingBot(), interactor.getPlugin(), "interactor");
        interactors.add(interactor);
    }

    @Override
    public void unregisterInteractors(Plugin plugin) {
        RegisterUtility.checkUnregister(getXiaomingBot(), plugin, "interactor");
        interactors.removeIf(interactor -> (interactor.getPlugin() == plugin));
    }

    @Override
    public void unregisterInteractors(Interactors interactors) {
        this.interactors.removeIf(interactor -> {
            if (interactor.getInteractors() != interactors) {
                return false;
            }
            RegisterUtility.checkUnregister(getXiaomingBot(), interactor.getPlugin(), "interactor");
            return true;
        });
    }
}