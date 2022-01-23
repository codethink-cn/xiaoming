package cn.chuanwise.xiaoming.util;

import cn.chuanwise.api.ChineseConvertable;
import cn.chuanwise.api.TagMarkable;
import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.verify.VerifyCodeHandler;
import cn.chuanwise.util.*;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.apply.ApplyHandler;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.contact.XiaomingContact;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.LanguageRenderContext;
import cn.chuanwise.xiaoming.language.variable.VariableRequester;

import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.*;
import java.util.function.BiFunction;

public class LanguageConfigUtil extends StaticUtil {
    public static void config(LanguageManager languageManager) {
        languageManager.registerConvertor(Optional.class, x -> Objects.toString(x.map(languageManager::convert).orElse(null)), null);
        languageManager.registerConvertor(Container.class, x -> Objects.toString(x.map(languageManager::convert).orElse(null)), null);

        registerPluginRelated(languageManager);
        registerFileSaverRelated(languageManager);
        registerGlobalVariables(languageManager);
        registerApplyRelated(languageManager);
        registerUserRelated(languageManager);
        registerBotRelated(languageManager);
        registerAccountRelated(languageManager);
        registerDateRelated(languageManager);
        registerGroupRelated(languageManager);
        registerInteractorRelated(languageManager);
        registerConfigurationRelated(languageManager);
        registerRandomRelated(languageManager);
        registerClassRelated(languageManager);
        registerStatisticianRelated(languageManager);
        registerMathRelated(languageManager);
        registerRuntimeRelated(languageManager);
        registerCollectionRelated(languageManager);
        registerContactRelated(languageManager);
        registerContextRelated(languageManager);
        registerReceptionistRelated(languageManager);
        registerGeneric(languageManager);
        registerSystemRelated(languageManager);
    }

    protected static <T> Object parameterOperator(T value, String identifier, BiFunction<Character, String, Object> consumer) {
        if (identifier.length() < 3 || identifier.charAt(1) != ':') {
            return null;
        }
        return consumer.apply(identifier.charAt(0), identifier.substring(2));
    }

    protected static void registerSystemRelated(LanguageManager languageManager) {
        languageManager.registerVariable("property", System.getProperties(), null);
        languageManager.registerOperators(Properties.class, null)
                .addOperator(Properties::getProperty);
    }

    /** 申请相关 */
    protected static void registerApplyRelated(LanguageManager languageManager) {
        languageManager.registerOperators(ApplyHandler.class, null)
                .addOperator("message", ApplyHandler::getMessage)
                .addOperator("permissions", ApplyHandler::getPermissions)
                .addOperator("submitter", handler -> Optional.ofNullable(handler.getPlugin()).map(Plugin::getName).orElse("内核"))
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
                .addOperator("interactorManager", XiaomingBot::getInteractorManager)
                .addOperator("fileSaver", XiaomingBot::getFileSaver)
                .addOperator("github", XiaomingBot.GITHUB)
                .addOperator("group", XiaomingBot.GROUP)
                .addOperator("sponsor", XiaomingBot.SPONSOR)
                .addOperator("version", XiaomingBot.VERSION)
                .addOperator("development-document", XiaomingBot.DEVELOPMENT_DOCUMENT)
                .addOperator("receptionistManager", XiaomingBot::getReceptionistManager)
                .addOperator("pluginManager", XiaomingBot::getPluginManager)
                .addOperator("lang", XiaomingBot::getLanguageManager)
                .addOperator("code", value -> value.getCode())
                .addOperator("codeString", value -> Long.toString(value.getCode()));
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
                        .addOperator("enabledPlugins", manager -> CollectionUtil.filter(manager.getPlugins().values(), plugin -> plugin.getHandler().isEnabled()))
                .addOperator("loadedPlugins", manager -> CollectionUtil.filter(manager.getPlugins().values(), plugin -> plugin.getHandler().isLoaded()));
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
        languageManager.registerVariable("num", 0, null);

        languageManager.registerOperators(Number.class, null)
                .addOperator("int", Number::intValue)
                .addOperator("double", Number::doubleValue)
                .addOperator("delay", value -> TimeUtil.futureTimeLength(value.longValue()))
                .addOperator("after", value -> TimeUtil.after(value.longValue()))
                .addOperator("date", value -> new Date(value.longValue()))
                .addOperator("account", value -> languageManager.getXiaomingBot().getAccountManager().createAccount(value.longValue()))
                .addOperator("alias", value -> languageManager.getXiaomingBot().getAccountManager().getAliasOrCode(value.longValue()))
                .addOperator("length", value -> TimeUtil.toTimeLength(value.longValue()));
        languageManager.registerOperators(Double.class, null)
                .addOperator("abs", Math::abs)
                .addOperator((value, identifier) -> {
                    if (identifier.length() < 2) {
                        return null;
                    }
                    final char operator = identifier.charAt(0);
                    final Optional<Double> optionalRight = NumberUtil.parseDouble(identifier.substring(1));
                    final Double left = value;

                    // 数字格式错误时
                    if (!optionalRight.isPresent()) {
                        return null;
                    }

                    final Double right = optionalRight.get();

                    switch (operator) {
                        case '+':
                            return left + right;
                        case '-':
                            return left - right;
                        case '*':
                            return left * right;
                        case '/':
                            if (right == 0) {
                                return "(" + left + "/0)";
                            } else {
                                return left / right;
                            }
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
                    final Optional<Integer> optionalRight = NumberUtil.parseInteger(identifier.substring(1));
                    final Integer left = value;

                    // 数字格式错误时
                    if (!optionalRight.isPresent()) {
                        return null;
                    }
                    final Integer right = optionalRight.get();

                    switch (operator) {
                        case '+':
                            return left + right;
                        case '-':
                            return left - right;
                        case '*':
                            return left * right;
                        case '/':
                            if (right == 0) {
                                return "(" + left + "/0)";
                            } else {
                                return left / right;
                            }
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
                .addOperator("code", XiaomingUser::getCode)
                .addOperator("codeString", XiaomingUser::getCodeString)
                .addOperator("alias", XiaomingUser::getAliasOrName)
                .addOperator("tags", XiaomingUser::getTags)
                .addOperator("contact", XiaomingUser::getContact)
                .addOperator("aliasOrCode", XiaomingUser::getAliasOrCode)
                .addOperator("aliasAndCode", XiaomingUser::getAliasAndCode)
                .addOperator((user, identifier) -> user.getProperty(PropertyType.valueOf(identifier)).orElse(null));
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
                .addOperator("tags", XiaomingContact::getTags)
                .addOperator("name", XiaomingContact::getName)
                .addOperator("code", XiaomingContact::getCode)
                .addOperator("codeString", XiaomingContact::getCodeString)
                .addOperator("alias", XiaomingContact::getAlias);
    }

    /** 日期相关 */
    protected static void registerDateRelated(LanguageManager languageManager) {
        languageManager.registerVariable("date", Date::new, null);
        languageManager.registerConvertor(Date.class, TimeUtil::format, null);

        languageManager.registerOperators(Date.class, null)
                .addOperator("millis", Date::getTime)
                .addOperator("long", Date::getTime);
    }

    /** 群聊相关 */
    protected static void registerGroupRelated(LanguageManager languageManager) {
        languageManager.registerConvertor(GroupInformation.class, GroupInformation::getAliasAndCode, null);
        languageManager.registerOperators(GroupInformation.class, null)
                .addOperator("alias", GroupInformation::getAlias)
                .addOperator("aliasAndCode", GroupInformation::getAliasAndCode)
                .addOperator("aliasOrCode", GroupInformation::getAlias)
                .addOperator("tags", TagMarkable::getTags);

        languageManager.registerOperators(Integer.class, null)
                .addOperator("group", languageManager.getXiaomingBot().getGroupInformationManager()::forCode);
        languageManager.registerOperators(String.class, null)
                .addOperator("group", languageManager.getXiaomingBot().getGroupInformationManager()::searchGroupsByTag);
    }

    protected static void registerGlobalVariables(LanguageManager languageManager) {
        languageManager.registerVariable("bot", languageManager.getXiaomingBot(), null);
    }

    protected static void registerInteractorRelated(LanguageManager languageManager) {
        languageManager.registerOperators(InteractorManager.class, null)
                .addOperator((manager, identifier) -> {
                    final List<Interactor> interactors = manager.getInteractors();
                    return CollectionUtil.first(interactors, interactor -> (Objects.equals(identifier, identifier.getClass().getName())));
                });
        languageManager.registerConvertor(Interactor.class, Interactor::getName, null);

        languageManager.registerOperators(Interactor.class, null)
                .addOperator("name", Interactor::getName)
                .addOperator("permissions", Interactor::getPermissions)
                .addOperator("usage", Interactor::getUsage)
                .addOperator("requireGroupTags", Interactor::getRequireGroupTags)
                .addOperator("requireAccountTags", Interactor::getRequireAccountTags);
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
                .addOperator("savePeriod", Configuration::getSavePeriod)
                .addOperator("saveFileDirectly", Configuration::isSaveFileDirectly)
                .addOperator("optimizePeriod", Configuration::getOptimizePeriod);
    }

    protected static void registerRandomRelated(LanguageManager languageManager) {
        languageManager.registerVariable("random", RandomUtil.getRandom(), null);
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
                }, (collection, index) -> CollectionUtil.arrayGet(collection, Integer.parseInt(index)))
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
                    return CollectionUtil.toString(((Collection<Object>) collection), languageManager::convert);
                })
                .addOperator("toIndexString", collection -> {
                    return CollectionUtil.toIndexString(((Collection<Object>) collection), languageManager::convert);
                });
        languageManager.registerConvertor(Collection.class, collection -> CollectionUtil.toString(collection, languageManager::convert), null);

        // 字符串相关
        languageManager.registerOperators(String.class, null)
                .addOperator("length", String::length)
                .addOperator("toUpperCase", String::toUpperCase)
                .addOperator("toLowerCase", String::toLowerCase)
                .addOperator("repeat", string -> (string + string))
                .addOperator("tagGroups", group -> xiaomingBot.getGroupInformationManager().searchGroupsByTag(group))
                .addOperator("reverse", StringUtil::reverse);

        // 映射相关
        languageManager.registerOperators(Map.class, null)
                .addOperator("size", Map::size)
                .addOperator((map, key) -> map.containsKey(key), Map::get);
        languageManager.registerConvertor(Map.class, map -> {
            final Set<Map.Entry> iterable = map.entrySet();
            return StringUtil.firstNonEmpty(CollectionUtil.toIndexString(iterable, entry -> {
                return languageManager.convert(entry.getKey()) + "：" + languageManager.convert(entry.getValue());
            }), "（空）");
        }, null);
    }

    protected static void registerStatisticianRelated(LanguageManager languageManager) {
        languageManager.registerOperators(Statistician.class, null)
                .addOperator("startTime", Statistician::getBeginTime)
                .addOperator("callNumber", Statistician::getCallNumber)
                .addOperator("effectiveCallNumber", Statistician::getEffectiveCallNumber);
    }

    protected static void registerContextRelated(LanguageManager languageManager) {
        languageManager.registerOperators(LanguageRenderContext.class, null)
                .addOperator((context, identifier) -> context.getParameterNames().contains(identifier),
                        (context, identifier) -> {
                            final int index = context.getParameterNames().indexOf(identifier);
                            final Object[] values = context.getValues();
                            if (IndexUtil.isLegal(index, values.length)) {
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

    protected static void registerReceptionistRelated(LanguageManager languageManager) {
        final XiaomingBot xiaomingBot = languageManager.getXiaomingBot();
        languageManager.registerOperators(Receptionist.class, null)
                .addOperator("code", Receptionist::getCode)
                .addOperator("alias", receptionist -> xiaomingBot.getAccountManager().getAliasOrCode(receptionist.getCode()));
        languageManager.registerConvertor(Receptionist.class, receptionist -> {
            return xiaomingBot.getAccountManager().getAliasAndCode(receptionist.getCode());
        }, null);
    }

    protected static void printMethodNames() {
        ReflectUtil.forEachDeclaredStaticMethod(LanguageConfigUtil.class, (clazz, method) -> {
            final String name = method.getName();
            if (name.startsWith("register")) {
                System.out.println(name + "(languageManager);");
            }
        });
    }
}
