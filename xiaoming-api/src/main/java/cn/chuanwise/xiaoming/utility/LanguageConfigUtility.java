package cn.chuanwise.xiaoming.utility;

import cn.chuanwise.api.ChineseConvertable;
import cn.chuanwise.toolkit.verify.VerifyCodeHandler;
import cn.chuanwise.utility.*;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.apply.ApplyHandler;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.client.CenterClientManager;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.group.GroupRecord;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.MultipleLanguageFinder;
import cn.chuanwise.xiaoming.language.sentence.LanguageRenderContext;
import cn.chuanwise.xiaoming.language.sentence.Sentence;
import cn.chuanwise.xiaoming.language.variable.VariableRequester;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.limit.CallLimitConfiguration;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.permission.PermissionAccessible;
import cn.chuanwise.xiaoming.permission.PermissionGroup;
import cn.chuanwise.xiaoming.permission.PermissionManager;
import cn.chuanwise.xiaoming.permission.PermissionUserNode;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.attribute.AttributeType;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.tag.TagHolder;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.*;
import java.util.function.BiFunction;

public class LanguageConfigUtility extends StaticUtility {
    public static void config(LanguageManager languageManager) {
        registerLicenseRelated(languageManager);
        registerPluginRelated(languageManager);
        registerFileSaverRelated(languageManager);
        registerGlobalVariables(languageManager);
        registerApplyRelated(languageManager);
        registerUserRelated(languageManager);
        registerBotRelated(languageManager);
        registerPermissionRelated(languageManager);
        registerAccountRelated(languageManager);
        registerDateRelated(languageManager);
        registerGroupRelated(languageManager);
        registerInteractorRelated(languageManager);
        registerCallLimitRelated(languageManager);
        registerConfigurationRelated(languageManager);
        registerRandomRelated(languageManager);
        registerClassRelated(languageManager);
        registerLanguageRelated(languageManager);
        registerStatisticianRelated(languageManager);
        registerMathRelated(languageManager);
        registerRuntimeRelated(languageManager);
        registerCollectionRelated(languageManager);
        registerContactRelated(languageManager);
        registerContextRelated(languageManager);
        registerReceptionistRelated(languageManager);
        registerCenterClientRelated(languageManager);
        registerGeneric(languageManager);
    }

    protected static <T> Object parameterOperator(T value, String identifier, BiFunction<Character, String, Object> consumer) {
        if (identifier.length() < 3 || identifier.charAt(1) != ':') {
            return null;
        }
        return consumer.apply(identifier.charAt(0), identifier.substring(2));
    }

    /** 申请相关 */
    protected static void registerApplyRelated(LanguageManager languageManager) {
        languageManager.registerOperators(ApplyHandler.class, null)
                .addOperator("message", ApplyHandler::getMessage)
                .addOperator("permissions", ApplyHandler::getPermissions)
                .addOperator("submitter", handler -> FunctionalUtility.runIfArgumentNonNullOrDefault(Plugin::getName, handler.getPlugin(), "内核"))
                .addOperator("time", VerifyCodeHandler::getSubmitTime)
                .addOperator("timeout", VerifyCodeHandler::getTimeout)
                .addOperator("verifyCode", VerifyCodeHandler::getVerifyCode);
    }

    /** 内核相关 */
    protected static void registerBotRelated(LanguageManager languageManager) {
        languageManager.registerVariable("bot", languageManager.getXiaomingBot(), null);
        languageManager.registerOperators(XiaomingObject.class, null)
                .addOperator("bot", XiaomingObject::getXiaomingBot);

        languageManager.registerOperators(XiaomingBot.class, null)
                .addOperator("statistician", XiaomingBot::getStatistician)
                .addOperator("config", XiaomingBot::getConfiguration)
                .addOperator("configuration", XiaomingBot::getConfiguration)
                .addOperator("licenseManager", XiaomingBot::getLicenseManager)
                .addOperator("interactorManager", XiaomingBot::getInteractorManager)
                .addOperator("fileSaver", XiaomingBot::getFileSaver)
                .addOperator("github", XiaomingBot.GITHUB)
                .addOperator("group", XiaomingBot.GROUP)
                .addOperator("sponsor", XiaomingBot.SPONSOR)
                .addOperator("version", XiaomingBot.COMPLETE_VERSION)
                .addOperator("development-document", XiaomingBot.DEVELOPMENT_DOCUMENT)
                .addOperator("receptionistManager", XiaomingBot::getReceptionistManager)
                .addOperator("pluginManager", XiaomingBot::getPluginManager)
                .addOperator("client", XiaomingBot::getCenterClientManager)
                .addOperator("lang", XiaomingBot::getLanguageManager)
                .addOperator("code", value -> value.getMiraiBot().getId())
                .addOperator("codeString", value -> Long.toString(value.getMiraiBot().getId()));
    }

    /** 插件相关 */
    protected static void registerPluginRelated(LanguageManager languageManager) {
        languageManager.registerConvertor(Plugin.class, Plugin::getCompleteName, null);
        languageManager.registerConvertor(PluginHandler.class, PluginHandler::getName, null);

        languageManager.registerOperators(PluginManager.class, null)
                .addOperator("plugins", PluginManager::getPlugins)
                .addOperator("pluginHandlers", PluginManager::getPluginHandlers)
                .addOperator(((value, identifier) -> parameterOperator(value, identifier, (operator, content) -> {
                    switch (operator) {
                        case 'p':
                            return value.getPlugin(content);
                        case 'h':
                            return value.getPluginHandler(content);
                        default:
                            return null;
                    }
                })))
                        .addOperator("enabledPlugins", manager -> CollectionUtility.filter(manager.getPlugins().values(), plugin -> plugin.getHandler().isEnabled()))
                .addOperator("loadedPlugins", manager -> CollectionUtility.filter(manager.getPlugins().values(), plugin -> plugin.getHandler().isLoaded()));
        languageManager.registerOperators(Plugin.class, null)
                .addOperator("name", Plugin::getName)
                .addOperator("version", Plugin::getVersion)
                .addOperator("alias", Plugin::getAlias)
                .addOperator("handler", Plugin::getHandler)
                .addOperator("interactors", plugin -> plugin.getXiaomingBot().getInteractorManager().getInteractors(plugin))
                .addOperator("listeners", plugin -> plugin.getXiaomingBot().getEventManager().getListeners(plugin));
        languageManager.registerOperators(PluginHandler.class, null)
                .addOperator("name", PluginHandler::getName)
                .addOperator("version", PluginHandler::getVersion)
                .addOperator("author", PluginHandler::getSingleAuthor)
                .addOperator("authors", PluginHandler::getMultipleAuthors)
                .addOperator("depends", PluginHandler::getDepends)
                .addOperator("softDepends", PluginHandler::getSoftDepends);
    }

    /** 类型相关 */
    protected static void registerClassRelated(LanguageManager languageManager) {
        languageManager.registerOperators(Object.class, null)
                .addOperator("class", Object::getClass);

        languageManager.registerConvertor(Class.class, Class::getName, null);
        languageManager.registerOperators(Class.class, null)
                .addOperator("name", Class::getName)
                .addOperator("simpleName", Class::getSimpleName);
    }

    /** 权限相关 */
    protected static void registerPermissionRelated(LanguageManager languageManager) {
        final PermissionManager permissionManager = languageManager.getXiaomingBot().getPermissionManager();
        languageManager.registerOperators(PermissionManager.class, null)
                .addOperator(((value, identifier) -> {
                    if (identifier.length() < 3 || identifier.charAt(1) != ':') {
                        return null;
                    }
                    final char ch = identifier.charAt(0);
                    final String content = identifier.substring(2);

                    switch (ch) {
                        case 'g':
                            return value.forPermissionGroup(content);
                        case 'u':
                            final Long parsedCode = NumberUtility.parseLong(content);
                            if (Objects.nonNull(parsedCode)) {
                                return value.forUserNode(parsedCode);
                            }
                            return null;
                        default:
                            return null;
                    }
                }));
        languageManager.registerConvertor(PermissionGroup.class, PermissionGroup::getAliasAndName, null);

        languageManager.registerOperators(PermissionGroup.class, null)
                .addOperator("name", PermissionGroup::getName)
                .addOperator("alias", PermissionGroup::getAlias)
                .addOperator("aliasAndName", PermissionGroup::getAliasAndName)
                .addOperator("aliasOrName", PermissionGroup::getAliasOrName)
                .addOperator("permissions", PermissionGroup::getPermissions)
                .addOperator("groupPermissions", PermissionGroup::getGroupPermissions)
                .addOperator("superGroups", PermissionGroup::getSuperGroups)
                .addOperator((value, identifier) -> parameterOperator(value, identifier, (operator, content) -> {
                    switch (operator) {
                        case 'h':
                            return permissionManager.permissionGroupAccessible(value, content) == PermissionAccessible.ACCESSABLE;
                        case 'g':
                            return value.getGroupPermission(content);
                        default:
                            return null;
                    }
                }));

        languageManager.registerOperators(PermissionUserNode.class, null)
                .addOperator("group", PermissionUserNode::getGroup)
                .addOperator("groupPermissions", PermissionUserNode::getGroupPermissions)
                .addOperator((value, identifier) -> parameterOperator(value, identifier, (operator, content) -> {
                    switch (operator) {
                        case 'g':
                            return value.getGroupPermission(content);
                        default:
                            return null;
                    }
                }));
    }

    /** 账户相关 */
    protected static void registerAccountRelated(LanguageManager languageManager) {
        languageManager.registerConvertor(Account.class, Account::getAliasAndCode, null);

        languageManager.registerOperators(Account.class, null)
                .addOperator("code", Account::getCode)
                .addOperator("codeString", Account::getCodeString)
                .addOperator("alias", Account::getAlias)
                .addOperator("name", Account::getAlias)
                .addOperator("aliasAndCode", Account::getAliasAndCode)
                .addOperator("aliasOrCode", Account::getAliasOrCode);
    }

    /** 数学相关 */
    protected static void registerMathRelated(LanguageManager languageManager) {
        languageManager.registerVariable("number", 0, null);

        languageManager.registerOperators(Number.class, null)
                .addOperator("int", Number::intValue)
                .addOperator("double", Number::doubleValue)
                .addOperator("delay", value -> TimeUtility.since(value.longValue()))
                .addOperator("after", value -> TimeUtility.after(value.longValue()))
                .addOperator("date", value -> new Date(value.longValue()))
                .addOperator("account", value -> languageManager.getXiaomingBot().getAccountManager().getAccount(value.longValue()))
                .addOperator("alias", value -> languageManager.getXiaomingBot().getAccountManager().getAliasOrCode(value.longValue()))
                .addOperator("length", value -> TimeUtility.toTimeLength(value.longValue()));
        languageManager.registerOperators(Double.class, null)
                .addOperator("abs", Math::abs)
                .addOperator((value, identifier) -> {
                    if (identifier.length() < 2) {
                        return null;
                    }
                    final char operator = identifier.charAt(0);
                    final Double right = NumberUtility.parseDouble(identifier.substring(1));
                    final Double left = value;

                    // 数字格式错误时
                    if (Objects.isNull(right)) {
                        return null;
                    }

                    switch (operator) {
                        case '+':
                            return left + right;
                        case '-':
                            return left - right;
                        case '*':
                            return left * right;
                        case '/':
                            return left / right;
                        case '%':
                            return left % right;
                        default:
                            return null;
                    }
                });
        languageManager.registerOperators(Integer.class, null)
                .addOperator("abs", Math::abs)
                .addOperator((value, identifier) -> {
                    if (identifier.length() < 2) {
                        return null;
                    }
                    final char operator = identifier.charAt(0);
                    final Integer right = NumberUtility.parseInteger(identifier.substring(1));
                    final Integer left = value;

                    // 数字格式错误时
                    if (Objects.isNull(right)) {
                        return null;
                    }

                    switch (operator) {
                        case '+':
                            return left + right;
                        case '-':
                            return left - right;
                        case '*':
                            return left * right;
                        case '/':
                            return left / right;
                        case '%':
                            return left % right;
                        default:
                            return null;
                    }
                });
    }

    /** 用户相关 */
    protected static void registerUserRelated(LanguageManager languageManager) {
        languageManager.registerConvertor(XiaomingUser.class, XiaomingUser::getAliasAndCode, null);

        languageManager.registerOperators(XiaomingUser.class, null)
                .addOperator("name", XiaomingUser::getName)
                .addOperator((user, identifier) -> user.getAttribute(AttributeType.valueOf(identifier)))
                .addOperator("code", XiaomingUser::getCode)
                .addOperator("codeString", XiaomingUser::getCodeString)
                .addOperator("alias", XiaomingUser::getAlias)
                .addOperator("tags", XiaomingUser::getTags)
                .addOperator("contact", XiaomingUser::getContact)
                .addOperator("aliasOrCode", XiaomingUser::getAliasOrCode)
                .addOperator("aliasAndCode", XiaomingUser::getAliasAndCode);
        languageManager.registerOperators(GroupXiaomingUser.class, null)
                .addOperator("nick", GroupXiaomingUser::getNick)
                .addOperator("nameCard", GroupXiaomingUser::getNameCard)
                .addOperator("groupCode", GroupXiaomingUser::getGroupCode);
        languageManager.registerOperators(MemberXiaomingUser.class, null)
                .addOperator("nick", MemberXiaomingUser::getGroupContact)
                .addOperator("nameCard", MemberXiaomingUser::getNameCard);
    }

    /** 会话相关 */
    protected static void registerContactRelated(LanguageManager languageManager) {
        languageManager.registerConvertor(XiaomingContact.class, XiaomingContact::getAliasAndCode, null);

        languageManager.registerOperators(XiaomingContact.class, null)
                .addOperator("name", XiaomingContact::getName)
                .addOperator("code", XiaomingContact::getCode)
                .addOperator("codeString", XiaomingContact::getCodeString)
                .addOperator("alias", XiaomingContact::getAlias);
        languageManager.registerOperators(GroupContact.class, null)
                .addOperator("tags", GroupContact::getTags);
    }

    /** 日期相关 */
    protected static void registerDateRelated(LanguageManager languageManager) {
        languageManager.registerVariable("date", Date::new, null);
        languageManager.registerConvertor(Date.class, TimeUtility::format, null);

        languageManager.registerOperators(Date.class, null)
                .addOperator("millis", Date::getTime)
                .addOperator("long", Date::getTime);
    }

    /** 群聊相关 */
    protected static void registerGroupRelated(LanguageManager languageManager) {
        languageManager.registerConvertor(GroupRecord.class, GroupRecord::getAliasAndCode, null);
        languageManager.registerOperators(GroupRecord.class, null)
                .addOperator("alias", GroupRecord::getAlias)
                .addOperator("aliasAndCode", GroupRecord::getAliasAndCode)
                .addOperator("aliasOrCode", GroupRecord::getAlias)
                .addOperator("tags", TagHolder::getTags);

        languageManager.registerOperators(Integer.class, null)
                .addOperator("group", languageManager.getXiaomingBot().getGroupRecordManager()::forCode);
        languageManager.registerOperators(String.class, null)
                .addOperator("group", languageManager.getXiaomingBot().getGroupRecordManager()::forTag);
    }

    protected static void registerGlobalVariables(LanguageManager languageManager) {
        languageManager.registerVariable("bot", languageManager.getXiaomingBot(), null);
    }

    protected static void registerInteractorRelated(LanguageManager languageManager) {
        languageManager.registerOperators(InteractorManager.class, null)
                .addOperator((manager, identifier) -> {
                    final List<InteractorHandler> interactors = manager.getInteractors();
                    return CollectionUtility.first(interactors, interactor -> (Objects.equals(identifier, identifier.getClass().getName())));
                });
        languageManager.registerConvertor(InteractorHandler.class, InteractorHandler::getName, null);

        languageManager.registerOperators(InteractorHandler.class, null)
                .addOperator("name", InteractorHandler::getName)
                .addOperator("permissionsArray", InteractorHandler::getPermissions)
                .addOperator("permissions", InteractorHandler::listPermissions)
                .addOperator("usage", InteractorHandler::getUsage)
                .addOperator("requireGroupTags", InteractorHandler::getRequireGroupTags)
                .addOperator("requireAccountTags", InteractorHandler::getRequireAccountTags);
    }

    protected static void registerCallLimitRelated(LanguageManager languageManager) {
        languageManager.registerOperators(CallLimitConfiguration.class, null)
                .addOperator("period", CallLimitConfiguration::getPeriod)
                .addOperator("top", CallLimitConfiguration::getTop)
                .addOperator("cooldown", CallLimitConfiguration::getCoolDown)
                .addOperator("deltaNoticeTime", CallLimitConfiguration::getDeltaNoticeTime);
    }

    protected static void registerGeneric(LanguageManager languageManager) {
        languageManager.registerConvertor(ChineseConvertable.class, ChineseConvertable::toChinese, null);

        languageManager.registerOperators(String[].class, null)
                .addOperator("size", array -> array.length)
                .addOperator("length", array -> array.length)
                .addOperator((array, identifier) -> {
                    if (identifier.matches("\\d+")) {
                        final int index = Integer.parseInt(identifier);
                        if (index < array.length) {
                            return array[index];
                        }
                    }
                    return null;
                });

        languageManager.registerOperators(Throwable.class, null)
                .addOperator("cause", Throwable::getCause);
    }

    protected static void registerConfigurationRelated(LanguageManager languageManager) {
        languageManager.registerOperators(Configuration.class, null)
                .addOperator("groupCallConfig", Configuration::getGroupCallConfig)
                .addOperator("savePeriod", Configuration::getSavePeriod)
                .addOperator("saveFileDirectly", Configuration::isSaveFileDirectly)
                .addOperator("privateCallConfig", Configuration::getPrivateCallConfig)
                .addOperator("optimizePeriod", Configuration::getOptimizePeriod)
                .addOperator("enableClearCall", Configuration::isEnableClearCall)
                .addOperator("clearCallGroupTag", Configuration::getClearCallGroupTag)
                .addOperator("clearCallPrefixs", Configuration::getClearCallPrefixes);
    }

    protected static void registerRandomRelated(LanguageManager languageManager) {
        languageManager.registerVariable("random", RandomUtility.getRandom(), null);
        languageManager.registerOperators(Random.class, null)
                .addOperator("int", Random::nextInt)
                .addOperator("long", Random::nextLong)
                .addOperator("double", Random::nextDouble);
    }

    protected static void registerRuntimeRelated(LanguageManager languageManager) {
        languageManager.registerVariable("runtime", Runtime::getRuntime, null);
        languageManager.registerOperators(Runtime.class, null)
                .addOperator("memoryRate", value -> (1 - ((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory())));
    }

    protected static void registerCollectionRelated(LanguageManager languageManager) {
        // 集合相关
        final XiaomingBot xiaomingBot = languageManager.getXiaomingBot();

        languageManager.registerOperators(List.class, null)
                .addOperator((list, index) -> {
                    if (index.matches("\\d+")) {
                        final int i = Integer.parseInt(index);
                        return i < list.size();
                    } else {
                        return false;
                    }
                }, (list, index) -> list.get(Integer.parseInt(index)));
        languageManager.registerOperators(Collection.class, null)
                .addOperator((collection, index) -> {
                    if (index.matches("\\d+")) {
                        final int i = Integer.parseInt(index);
                        return i < collection.size();
                    } else {
                        return false;
                    }
                }, (collection, index) -> CollectionUtility.arrayGet(collection, Integer.parseInt(index)))
                .addOperator(new VariableRequester<Collection>() {
                    @Override
                    public Object request(Collection value, String identifier) {
                        return value.size();
                    }

                    @Override
                    public boolean apply(Collection value, String identifier) {
                        return Objects.equals(identifier, "size");
                    }
                })
                .addOperator(new VariableRequester<Collection>() {
                    @Override
                    public Object request(Collection value, String identifier) {
                        return value.isEmpty();
                    }

                    @Override
                    public boolean apply(Collection value, String identifier) {
                        return Objects.equals(identifier, "isEmpty");
                    }
                })
                .addOperator("toSimpleString", collection -> {
                    return CollectionUtility.toString(((Collection<Object>) collection), languageManager::convert);
                })
                .addOperator("toIndexString", collection -> {
                    return CollectionUtility.toIndexString(((Collection<Object>) collection), languageManager::convert);
                });
        languageManager.registerConvertor(Collection.class, collection -> CollectionUtility.toString(collection, languageManager::convert), null);

        // 字符串相关
        languageManager.registerOperators(String.class, null)
                .addOperator("length", String::length)
                .addOperator("toUpperCase", String::toUpperCase)
                .addOperator("toLowerCase", String::toLowerCase)
                .addOperator("repeat", string -> (string + string))
                .addOperator("tagGroups", group -> xiaomingBot.getGroupRecordManager().forTag(group))
                .addOperator("permissionGroup", string -> xiaomingBot.getPermissionManager().forPermissionGroup(string))
                .addOperator("reverse", StringUtility::reverse);

        // 映射相关
        languageManager.registerOperators(Map.class, null)
                .addOperator("size", Map::size)
                .addOperator((map, key) -> map.containsKey(key), Map::get);
        languageManager.registerConvertor(Map.class, map -> {
            final Set<Map.Entry> iterable = map.entrySet();
            return StringUtility.firstNonEmpty(CollectionUtility.toIndexString(iterable, entry -> {
                return languageManager.convert(entry.getKey()) + "：" + languageManager.convert(entry.getValue());
            }), "（空）");
        }, null);
    }

    protected static void registerLanguageRelated(LanguageManager languageManager) {
        languageManager.registerOperators(LanguageManager.class, null)
                .addOperator(LanguageManager::getSentence);
        languageManager.registerOperators(MultipleLanguageFinder.class, null)
                .addOperator(MultipleLanguageFinder::getSentence);
        languageManager.registerConvertor(Sentence.class, Sentence::getDefaultValue, null);
        languageManager.registerOperators(Sentence.class, null)
                .addOperator("defaultValue", Sentence::getDefaultValue)
                .addOperator("customValues", Sentence::getCustomValues);
    }

    protected static void registerStatisticianRelated(LanguageManager languageManager) {
        languageManager.registerOperators(Statistician.class, null)
                .addOperator("startTime", Statistician::getBeginTime)
                .addOperator("callNumber", Statistician::getCallNumber);
    }

    protected static void registerContextRelated(LanguageManager languageManager) {
        languageManager.registerOperators(LanguageRenderContext.class, null)
                .addOperator((context, identifier) -> context.getParameterNames().contains(identifier),
                        (context, identifier) -> {
                            final int index = context.getParameterNames().indexOf(identifier);
                            final Object[] values = context.getValues();
                            if (IndexUtility.isLegal(index, values.length)) {
                                return values[index];
                            } else {
                                return "[[ctx-var: " + identifier + "]]";
                            }
                        });
    }

    protected static void registerFileSaverRelated(LanguageManager languageManager) {
        languageManager.registerOperators(FileSaver.class, null)
                .addOperator("preservables", FileSaver::getPreservables)
                .addOperator("lastSaveTime", FileSaver::getLastSaveTime)
                .addOperator("lastValidSaveTime", FileSaver::getLastValidSaveTime);
    }

    protected static void registerLicenseRelated(LanguageManager languageManager) {
        languageManager.registerOperators(LicenseManager.class, null)
                .addOperator("license", LicenseManager::getLicense);
    }

    protected static void registerCenterClientRelated(LanguageManager languageManager) {
        languageManager.registerOperators(CenterClientManager.class, null)
                .addOperator("totalCallNumber", client -> {
                    try {
                        return client.getTotalCallNumber();
                    } catch (Exception exception) {
                        return null;
                    }
                });
    }

    protected static void registerReceptionistRelated(LanguageManager languageManager) {
        final XiaomingBot xiaomingBot = languageManager.getXiaomingBot();
        languageManager.registerOperators(Receptionist.class, null)
                .addOperator("code", Receptionist::getCode)
                .addOperator("isBusy", Receptionist::isBusy)
                .addOperator("groupTasks", Receptionist::getGroupTasks)
                .addOperator("privateTask", Receptionist::getPrivateTask)
                .addOperator("memberTasks", Receptionist::getMemberTasks)
                .addOperator("alias", receptionist -> xiaomingBot.getAccountManager().getAliasOrCode(receptionist.getCode()));
        languageManager.registerConvertor(Receptionist.class, receptionist -> {
            return xiaomingBot.getAccountManager().getAliasAndCode(receptionist.getCode()) + "：" + (receptionist.isBusy() ? "忙碌" : "空闲");
        }, null);
    }

    protected static void printMethodNames() {
        ReflectUtility.forEachDeclaredStaticMethod(LanguageConfigUtility.class, (clazz, method) -> {
            final String name = method.getName();
            if (name.startsWith("register")) {
                System.out.println(name + "(languageManager);");
            }
        });
    }
}
