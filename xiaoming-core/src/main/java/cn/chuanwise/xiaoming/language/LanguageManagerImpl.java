package cn.chuanwise.xiaoming.language;

import cn.chuanwise.api.ChineseConvertable;
import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.utility.*;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.interactor.information.InteractorMethodInformation;
import cn.chuanwise.xiaoming.language.environment.*;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.limit.CallLimitConfiguration;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.permission.PermissionGroup;
import cn.chuanwise.xiaoming.permission.PermissionUserNode;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.recept.*;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class LanguageManagerImpl extends ModuleObjectImpl implements LanguageManager {
    final Environment environment;

    final Map<String, Supplier<Object>> variables = new ConcurrentHashMap<>();
    final List<Language> languages = new ArrayList<>();
    final List<StringConvertor> stringConvertors = new ArrayList<>();

    final File directory;

    public LanguageManagerImpl(XiaomingBot xiaomingBot, File directory) {
        super(xiaomingBot);
        this.directory = directory;
        this.environment = new EnvironmentImpl(xiaomingBot);
        registers();
    }

    private void registers() {
        // 基础变量
        // 时间相关
        registerVariable("time", () -> System.currentTimeMillis(), null);
        registerVariable("runtime", Runtime.getRuntime(), null);

        registerConvertor(ChineseConvertable.class, ChineseConvertable::toChinese, null);

        registerOperator(Runtime.class, null)
                .register("memoryRate", value -> (1 - ((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory())));

        registerConvertor(Date.class, TimeUtility::format, null);
        // 随机数相关
        registerVariable("random", RandomUtility.getRandom(), null);
        registerOperator(Random.class, null)
                .register("int", Random::nextInt)
                .register("long", Random::nextLong)
                .register("double", Random::nextDouble);

        // 数学相关
        registerVariable("number", 0, null);
        registerOperator(Number.class, null)
                .register("int", Number::intValue)
                .register("double", Number::doubleValue)
                .register((value, identifier) -> identifier.matches("[\\+\\-\\*\\/\\%]\\d+(\\.\\d+)?"), (value, identifier) -> {
                    final char opertaor = identifier.charAt(0);
                    final double number1 = value.doubleValue();
                    final double number2 = Double.parseDouble(identifier.substring(1));

                    final double result;
                    switch (opertaor) {
                        case '+':
                            result = number1 + number2;
                            break;
                        case '-':
                            result = number1 - number2;
                            break;
                        case '*':
                            result = number1 * number2;
                            break;
                        case '/':
                            result = number1 / number2;
                            break;
                        case '%':
                            result = number1 % number2;
                            break;
                        default:
                            throw new UnsupportedVersionException();
                    }

                    if (identifier.contains(".")) {
                        return result;
                    } else {
                        return (int) result;
                    }
                })
                .register("delay", value -> TimeUtility.since(value.longValue()))
                .register("after", value -> TimeUtility.after(value.longValue()))
                .register("date", value -> new Date(value.longValue()))
                .register("account", value -> getXiaomingBot().getAccountManager().forAccount(value.longValue()))
                .register("alias", value -> getXiaomingBot().getAccountManager().getAliasOrCode(value.longValue()))
                .register("length", value -> TimeUtility.toTimeLength(value.longValue()));
        registerOperator(Double.class, null)
                .register("abs", Math::abs);
        registerOperator(Integer.class, null)
                .register("abs", Math::abs);
        registerConvertor(Account.class, Account::getAliasAndCode, null);

        registerOperator(PermissionUserNode.class, null)
                .register("group", PermissionUserNode::getGroup)
                .register("groupPermissions", PermissionUserNode::getGroupPermissions)
                .register("permissions", PermissionUserNode::getPermissions);

        registerOperator(PermissionGroup.class, null)
                .register("name", PermissionGroup::getName)
                .register("alias", group -> ObjectUtility.firstNonNull(group.getAlias(), "（无）"))
                .register("supers", PermissionGroup::getSuperGroups)
                .register("permissions", PermissionGroup::getPermissions)
                .register("groupPermissions", PermissionGroup::getGroupPermissions);
        registerConvertor(PermissionGroup.class, PermissionGroup::getAliasAndName, null);

        // 类型相关
        registerOperator(Object.class, null)
                .register("class", Object::getClass);
        registerOperator(Class.class, null)
                .register("name", Class::getName)
                .register("simpleName", Class::getSimpleName);

        // 内核变量
        registerOperator(XiaomingObject.class, null)
                .register("bot", XiaomingObject::getXiaomingBot);

        registerVariable("lang", this, null);
        registerConvertor(Sentence.class, Sentence::getValue, null);

        registerVariable("bot", getXiaomingBot(), null);
        registerOperator(XiaomingBot.class, null)
                .register("statistician", XiaomingBot::getStatistician)
                .register("config", XiaomingBot::getConfiguration)
                .register("configuration", XiaomingBot::getConfiguration)
                .register("licenseManager", XiaomingBot::getLicenseManager)
                .register("interactorManager", XiaomingBot::getInteractorManager)
                .register("fileSaver", XiaomingBot::getFileSaver)
                .register("github", XiaomingBot.GITHUB)
                .register("group", XiaomingBot.GROUP)
                .register("sponsor", XiaomingBot.SPONSOR)
                .register("version", XiaomingBot.VERSION)
                .register("development-document", XiaomingBot.DEVELOPMENT_DOCUMENT)
                .register("receptionistManager", XiaomingBot::getReceptionistManager)
                .register("lang", XiaomingBot::getLanguageManager);

        registerOperator(ReceptionistManager.class, null)
                .register("receptionists", ReceptionistManager::getReceptionists);

        registerOperator(FileSaver.class, null)
                .register("preservables", FileSaver::getPreservables)
                .register("lastSaveTime", FileSaver::getLastSaveTime)
                .register("lastValidSaveTime", FileSaver::getLastValidSaveTime);

        registerOperator(Throwable.class, null)
                .register("cause", Throwable::getCause);


        registerOperator(InteractorManager.class, null)
                .register((manager, identifier) -> {
                    final Interactor sameNameCoreInteractor = CollectionUtility.first(manager.getCoreInteractors(), interactor -> Objects.equals(identifier.getClass().getSimpleName(), identifier));
                    if (Objects.nonNull(sameNameCoreInteractor)) {
                        return sameNameCoreInteractor;
                    }

                    for (Set<Interactor> interactors : manager.getPluginInteractors().values()) {
                        final Interactor sameNamePluginInteractor = CollectionUtility.first(interactors, interactor -> Objects.equals(identifier.getClass().getSimpleName(), identifier));
                        if (Objects.nonNull(sameNamePluginInteractor)) {
                            return sameNamePluginInteractor;
                        }
                    }

                    return null;
                });

        registerOperator(Interactor.class, null)
                .register(Interactor::forMethodInformation);
        registerOperator(InteractorMethodInformation.class, null)
                .register("name", InteractorMethodInformation::getName)
                .register("permissions", InteractorMethodInformation::getPermissions)
                .register("usages", InteractorMethodInformation::getUsages)
                .register("requireGroupTags", InteractorMethodInformation::getRequireGroupTags)
                .register("requireAccountTags", InteractorMethodInformation::getRequireAccountTags);

        registerOperator(String[].class, null)
                .register("size", array -> array.length)
                .register("length", array -> array.length)
                .register((array, identifier) -> {
                    if (identifier.matches("\\d+")) {
                        final int index = Integer.parseInt(identifier);
                        if (index < array.length) {
                            return array[index];
                        }
                    }
                    return null;
                });

        registerOperator(LicenseManager.class, null)
                .register("license", LicenseManager::getLicense);

        registerOperator(Configuration.class, null)
                .register("groupCallConfig", Configuration::getGroupCallConfig)
                .register("savePeriod", Configuration::getSavePeriod)
                .register("saveFileDirectly", Configuration::isSaveFileDirectly)
                .register("privateCallConfig", Configuration::getPrivateCallConfig)
                .register("optimizePeriod", Configuration::getOptimizePeriod)
                .register("enableClearCall", Configuration::isEnableClearCall)
                .register("clearCallGroupTag", Configuration::getClearCallGroupTag)
                .register("clearCallPrefixs", Configuration::getClearCallPrefixes);

        registerOperator(CallLimitConfiguration.class, null)
                .register("period", CallLimitConfiguration::getPeriod)
                .register("top", CallLimitConfiguration::getTop)
                .register("cooldown", CallLimitConfiguration::getCoolDown)
                .register("deltaNoticeTime", CallLimitConfiguration::getDeltaNoticeTime);

        // 插件
        registerOperator(XiaomingPlugin.class, null)
                .register("name", XiaomingPlugin::getName)
                .register("completeName", XiaomingPlugin::getCompleteName)
                .register("alias", XiaomingPlugin::getAlias)
                .register("version", XiaomingPlugin::getVersion);

        // 集合相关
        registerOperator(List.class, null)
                .register((list, index) -> {
                    if (index.matches("\\d+")) {
                        final int i = Integer.parseInt(index);
                        return i < list.size();
                    } else {
                        return false;
                    }
                }, (list, index) -> list.get(Integer.parseInt(index)));
        registerOperator(Collection.class, null)
                .register((collection, index) -> {
                    if (index.matches("\\d+")) {
                        final int i = Integer.parseInt(index);
                        return i < collection.size();
                    } else {
                        return false;
                    }
                }, (collection, index) -> CollectionUtility.arrayGet(collection, Integer.parseInt(index)))
                .register(new VariableHandler<Collection>() {
                    @Override
                    public Object onRequest(Collection value, String identifier) {
                        return value.size();
                    }

                    @Override
                    public boolean apply(Collection value, String identifier) {
                        return Objects.equals(identifier, "size");
                    }
                })
                .register(new VariableHandler<Collection>() {
                    @Override
                    public Object onRequest(Collection value, String identifier) {
                        return value.isEmpty();
                    }

                    @Override
                    public boolean apply(Collection value, String identifier) {
                        return Objects.equals(identifier, "isEmpty");
                    }
                })
                .register("toSimpleString", collection -> {
                    return CollectionUtility.toString(((Collection<Object>) collection), this::convertToString);
                })
                .register("toIndexString", collection -> {
                    return CollectionUtility.toIndexString(((Collection<Object>) collection), this::convertToString);
                });
        registerConvertor(Collection.class, collection -> CollectionUtility.toIndexString(collection, this::convertToString), null);

        // 字符串相关
        registerOperator(String.class, null)
                .register("length", String::length)
                .register("toUpperCase", String::toUpperCase)
                .register("toLowerCase", String::toLowerCase)
                .register("repeat", string -> (string + string))
                .register("tagGroups", group -> getXiaomingBot().getGroupRecordManager().forTag(group))
                .register("permissionGroup", string -> getXiaomingBot().getPermissionManager().forPermissionGroup(string))
                .register("reverse", StringUtility::reverse);

        registerConvertor(Boolean.class, value -> (value ? "是" : "否"), null);

        registerOperator(GroupRecord.class, null)
                .register("name", GroupRecord::getName)
                .register("alias", GroupRecord::getAlias)
                .register("tags", TagHolder::getTags);
        registerConvertor(GroupRecord.class, GroupRecord::getAliasAndCode, null);

        // 用户相关
        registerOperator(XiaomingUser.class, null)
                .register("code", XiaomingUser::getCode)
                .register("alias", XiaomingUser::getAlias)
                .register("codeString", XiaomingUser::getCodeString)
                .register("tags", XiaomingUser::getTags)
                .register("contact", XiaomingUser::getContact)
                .register("property", user -> user.getProperties());
        registerOperator(GroupXiaomingUser.class, null)
                .register("nick", GroupXiaomingUser::getNick)
                .register("nameCard", GroupXiaomingUser::getNameCard);
        registerOperator(MemberXiaomingUser.class, null)
                .register("nick", MemberXiaomingUser::getGroupContact)
                .register("nameCard", MemberXiaomingUser::getNameCard);
        registerConvertor(XiaomingUser.class, XiaomingUser::getAliasAndCode, null);
        registerOperator(XiaomingContact.class, null)
                .register("code", XiaomingContact::getCode)
                .register("codeString", XiaomingContact::getCodeString)
                .register("alias", XiaomingContact::getAlias);
        registerConvertor(XiaomingContact.class, XiaomingContact::getAliasAndCode, null);

        // 映射相关
        registerOperator(Map.class, null)
                .register("size", Map::size)
                .register((map, key) -> map.containsKey(key), Map::get);
        registerConvertor(Map.class, map -> {
            final Set<Map.Entry> iterable = map.entrySet();
            return StringUtility.firstNonEmpty(CollectionUtility.toIndexString(iterable, entry -> {
                return convertToString(entry.getKey()) + " => " + convertToString(entry.getValue());
            }), "（空）");
        }, null);

        registerOperator(Receptionist.class, null)
                .register("code", Receptionist::getCode)
                .register("isBusy", Receptionist::isBusy)
                .register("groupTasks", Receptionist::getGroupTasks)
                .register("privateTask", Receptionist::getPrivateTask)
                .register("memberTasks", Receptionist::getMemberTasks)
                .register("alias", receptionist -> getXiaomingBot().getAccountManager().getAliasOrCode(receptionist.getCode()));
        registerConvertor(Receptionist.class, receptionist -> {
            return getXiaomingBot().getAccountManager().getAliasAndCode(receptionist.getCode()) + "：" + (receptionist.isBusy() ? "忙碌" : "空闲");
        }, null);

        registerOperator(GroupReceptionTask.class, null)
                .register("name", task -> task.getUser().getContact().getCompleteName());
        registerConvertor(GroupReceptionTask.class, task -> {
            return task.getUser().getContact().getCompleteName() + "：" + (task.isBusy() ? "忙碌" : "空闲");
        }, null);

        registerOperator(MemberReceptionTask.class, null)
                .register("name", task -> task.getUser().getContact().getCompleteName());
        registerConvertor(MemberReceptionTask.class, task -> {
            return task.getUser().getContact().getCompleteName() + "：" + (task.isBusy() ? "忙碌" : "空闲");
        }, null);

        // 语言文件
        registerOperator(LanguageManager.class, null)
                .register((value, identifier) -> true, LanguageManager::getSentence);
        registerConvertor(Sentence.class, Sentence::getDefaultValue, null);
        registerOperator(Sentence.class, null)
                .register("defaultValue", Sentence::getDefaultValue)
                .register("customValue", Sentence::getCustomValues);

        // 调用次数
        registerOperator(Statistician.class, null)
                .register("startTime", Statistician::getBeginTime)
                .register("callNumber", Statistician::getCallNumber);

        // 上下文获取运算
        environment.register(SentenceContext.class)
                .register((context, identifier) -> context.getParameterNames().contains(identifier),
                        (context, identifier) -> context.getValues()[context.getParameterNames().indexOf(identifier)]);
    }

    @Override
    public String render(Sentence sentence, Function<String, Object> externalGetter, Object... arguments) {
        final String format = sentence.getValue();
        final String afterReplace = render(format, externalGetter);

        List<String> parameterNames = sentence.getDefaultParameterNames();
        return render0(afterReplace, parameterNames, externalGetter, arguments);
    }

    protected String render0(String format, List<String> parameterNames, Function<String, Object> externalGetter, Object[] arguments) {
        final String afterReplace = render(format, externalGetter);

//        CheckUtility.checkState(!getXiaomingBot().getConfiguration().isDebug() || parameterNames.size() == arguments.length,
//                "illegal arguments for format: " + format + ", details: \n" +
//                        "afterReplace: " + afterReplace + "\n" +
//                        "parameterName(s): " + parameterNames + " (size: " + parameterNames.size() + ")\n" +
//                        "parameters: " + Arrays.toString(arguments) + " (size: " + arguments.length + ")");

        final SentenceContext context = new SentenceContext(parameterNames, arguments);
        return render(afterReplace, variable -> {
            if (Objects.equals(variable, "context")) {
                return context;
            } else {
                return null;
            }
        });
    }

    @Override
    public String render(String format, Function<String, Object> externalGetter, Object... arguments) {
        final String afterReplace = render(format, externalGetter);
        final List<String> parameterNames = ArgumentUtility.getContextVariableNames(afterReplace);
        return render0(afterReplace, parameterNames, externalGetter, arguments);
    }
}
