package com.chuanwise.xiaoming.core.bot;

import com.chuanwise.toolkit.preservable.file.FileLoader;
import com.chuanwise.toolkit.preservable.file.loader.JsonFileLoader;
import com.chuanwise.toolkit.serialize.serializer.Serializer;
import com.chuanwise.toolkit.serialize.serializer.json.JackJsonSerializer;
import com.chuanwise.toolkit.serialize.serializer.json.JsonSerializer;
import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.classloader.XiaomingClassLoader;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.configuration.Statistician;
import com.chuanwise.xiaoming.api.contact.ContactManager;
import com.chuanwise.xiaoming.api.error.ReportMessageManager;
import com.chuanwise.xiaoming.api.exception.NoSuchBotException;
import com.chuanwise.xiaoming.api.exception.XiaomingInitializeException;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.language.Language;
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.api.util.FileUtils;
import com.chuanwise.xiaoming.api.util.PathUtils;
import com.chuanwise.xiaoming.api.event.EventManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.util.SerializerUtility;
import com.chuanwise.xiaoming.core.account.AccountManagerImpl;
import com.chuanwise.xiaoming.core.contact.ContactManagerImpl;
import com.chuanwise.xiaoming.core.contact.contact.ConsoleContactImpl;
import com.chuanwise.xiaoming.core.report.ReportMessageManagerImpl;
import com.chuanwise.xiaoming.core.interactor.core.ReportInteractor;
import com.chuanwise.xiaoming.core.interactor.InteractorManagerImpl;
import com.chuanwise.xiaoming.core.interactor.core.*;
import com.chuanwise.xiaoming.core.license.LicenceManagerImpl;
import com.chuanwise.xiaoming.core.response.ResponseGroupManagerImpl;
import com.chuanwise.xiaoming.core.thread.ConsoleInputThread;
import com.chuanwise.xiaoming.core.configuration.ConfigurationImpl;
import com.chuanwise.xiaoming.core.configuration.StatisticianImpl;
import com.chuanwise.xiaoming.core.schedule.SchedulerImpl;
import com.chuanwise.xiaoming.core.recept.ReceptionistManagerImpl;
import com.chuanwise.xiaoming.api.resource.ResourceManager;
import com.chuanwise.xiaoming.core.resource.ResourceManagerImpl;
import com.chuanwise.xiaoming.core.user.ConsoleXiaomingUserImpl;
import com.chuanwise.xiaoming.core.language.LanguageImpl;
import com.chuanwise.xiaoming.core.event.EventManagerImpl;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.core.limit.UserCallLimitManagerImpl;
import com.chuanwise.xiaoming.core.permission.PermissionManagerImpl;
import com.chuanwise.xiaoming.core.plugin.PluginManagerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotEvent;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * 小明机器人核心
 * @author Chuanwise
 */
@NoArgsConstructor
@Getter
@Slf4j
public class XiaomingBotImpl implements XiaomingBot {

    @Override
    public Logger getLog() {
        return log;
    }

    /**
     * mirai 机器人引用
     */
    Bot miraiBot;

    public XiaomingBotImpl(Bot miraiBot) {
        setMiraiBot(miraiBot);
    }

    public XiaomingBotImpl(long qq, String password) {
        this(BotFactory.INSTANCE.newBot(qq, password));
    }

    public XiaomingBotImpl(long qq, byte[] md5) {
        this(BotFactory.INSTANCE.newBot(qq, md5));
    }

    /**
     * 设置 mirai 机器人
     * @param miraiBot mirai 机器人
     */
    @Override
    public void setMiraiBot(Bot miraiBot) {
        if (Objects.nonNull(this.miraiBot) && miraiBot.isOnline()) {
            miraiBot.close();
        }
        this.miraiBot = miraiBot;
    }

    Map<String, Runnable> initializer = new HashMap<>();

    @Override
    public void load() {
        initializer.values().forEach(Runnable::run);
    }

    /**
     * 加载小明的某个组件
     * @param name 组件名
     * @return 是否找到该组件
     */
    @Override
    public boolean load(String name) {
        final Runnable runnable = initializer.get(name);
        if (Objects.nonNull(runnable)) {
            runnable.run();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 填充初始化列表
     * 小明将优先加载调度器等
     */
    protected void fillInitializer() {
        initializer.clear();

        // 先加载配置文件
        configuration = fileLoader.loadOrSupplie(ConfigurationImpl.class, new File(configurationDirectory, "configurations.json"), ConfigurationImpl::new);
        configuration.setXiaomingBot(this);

        // 加载主线程池
        scheduler = new SchedulerImpl(this);
        scheduler.start();

        // 添加自动任务
        scheduler.periodicRunLater(configuration.getOptimizePeriod(), configuration.getOptimizePeriod(), this::optimize).setDescription("自动优化性能");
        scheduler.periodicRunLater(configuration.getSavePeriod(), configuration.getSavePeriod(), scheduler.getPreservableSaveTask()).setDescription("自动保存文件");

        BiConsumer<File, String> checkIfFileExistAndLog = (file, type) -> {
            if (file.isFile()) {
                log.info("存在" + type + "：" + file.getAbsolutePath() + "，正在载入");
            } else {
                log.info("找不到" + type + "：" + file.getAbsolutePath() + "，将使用默认设置");
            }
        };
        BiConsumer<File, String> checkIfCanDeleteAndLog = (file, type) -> {
            if (file.isFile()) {
                log.warn(file.getAbsolutePath() + "，是早些内核版本的" + type + "，可以删除");
            }
        };

        checkIfCanDeleteAndLog.accept(new File("texts"), "文本文件");

        initializer.put("userCallLimitManager", () -> {
            userCallLimitManager = new UserCallLimitManagerImpl();
            userCallLimitManager.setXiaomingBot(this);
        });

        initializer.put("eventManager", () -> {
            eventManager = new EventManagerImpl(this);
        });

        initializer.put("interactorManager", () -> {
            interactorManager = new InteractorManagerImpl(this);
        });

        initializer.put("pluginManager", () -> {
            pluginManager = new PluginManagerImpl(this, pluginDirectory);
        });

        initializer.put("language", () -> {
            final File file = new File(configurationDirectory, "language.json");
            checkIfFileExistAndLog.accept(file, "语言文件");

            if (!file.exists()) {
                try {

                    FileUtils.copyResourceOrCreate("language.json", file);
                } catch (IOException exception) {
                    log.error("不存在语言文件，且无法复制默认语言文件");
                }
            }

            language = fileLoader.loadOrSupplie(LanguageImpl.class, file, LanguageImpl::new);
            language.setXiaomingBot(this);

            try {
                final InputStream resource = getClass().getClassLoader().getResourceAsStream("language.json");
                if (Objects.nonNull(resource)) {
                    log.info("正在更新语言文件");
                    final LanguageImpl defaultLanguage = coreSerializer.deserialize(resource, LanguageImpl.class);
                    defaultLanguage.setXiaomingBot(this);

                    // 追加新的内容
                    defaultLanguage.getValues().forEach((key, value) -> {
                        if (!language.getValues().containsKey(key)) {
                            language.put(key, value);
                            log.info("在语言文件中添加了新的内容：" + key + " => " + value);
                        }
                    });
                    getScheduler().readySave(language);
                } else {
                    log.error("无法找到用以更新旧语言文件的默认语言文件");
                }
            } catch (Exception exception) {
                log.error("更新旧语言文件时出现异常");
            }
        });

        initializer.put("permissionManager", () -> {
            final File file = new File(configurationDirectory, "permissions.json");
            checkIfFileExistAndLog.accept(file, "权限组文件");

            permissionManager = fileLoader
                    .loadOrSupplie(PermissionManagerImpl.class, file, () -> {
                        PermissionManagerImpl manager = new PermissionManagerImpl();
                        manager.setGroups(new HashMap<>());
                        return manager;
                    });
            permissionManager.setXiaomingBot(this);
        });

        initializer.put("statistician", () -> {
            final File file = new File(configurationDirectory, "statisticians.json");
            final String fileType = "统计数据文件";
            checkIfFileExistAndLog.accept(file, fileType);

            final File elderVersionFile = new File(configurationDirectory, "counters.json");
            checkIfCanDeleteAndLog.accept(elderVersionFile, fileType);

            statistician = fileLoader.loadOrSupplie(StatisticianImpl.class, file, StatisticianImpl::new);
            statistician.setXiaomingBot(this);
        });

        initializer.put("accountManager", () -> {
            accountManager = new AccountManagerImpl(this, accountDirectory);
        });

        initializer.put("responseGroupManager", () -> {
            final File file = new File(configurationDirectory, "groups.json");
            checkIfFileExistAndLog.accept(file, "响应群数据文件");

            responseGroupManager = fileLoader
                    .loadOrSupplie(ResponseGroupManagerImpl.class, file, ResponseGroupManagerImpl::new);
            responseGroupManager.setXiaomingBot(this);
            ((ResponseGroupManagerImpl) responseGroupManager).setGroups(((Set) responseGroupManager.getGroups()));
        });

        initializer.put("receptionistManager", () -> {
            receptionistManager = new ReceptionistManagerImpl(this);
        });

        initializer.put("reportMessageManager", () -> {
            final File file = new File(configurationDirectory, "reports.json");
            checkIfFileExistAndLog.accept(file, "反馈和错误报告文件");

            reportMessageManager = fileLoader
                    .loadOrSupplie(ReportMessageManagerImpl.class, file, ReportMessageManagerImpl::new);
            reportMessageManager.setXiaomingBot(this);
        });

        initializer.put("resourceManager", () -> {
            final File file = new File(resourceDirectory, "resources.json");
            checkIfFileExistAndLog.accept(file, "资源概况文件");

            resourceManager = fileLoader
                    .loadOrSupplie(ResourceManagerImpl.class, file, ResourceManagerImpl::new);
            resourceManager.setXiaomingBot(this);
            resourceManager.setResourceDirectory(resourceDirectory);
        });

        initializer.put("licenseManager", () -> {
            final File file = new File(configurationDirectory, "license.json");
            checkIfFileExistAndLog.accept(file, "小明协议验证数据");

            licenseManager = fileLoader
                    .loadOrSupplie(LicenceManagerImpl.class, file, LicenceManagerImpl::new);
            licenseManager.setXiaomingBot(this);
        });

        initializer.put("contactManager", () -> {
            contactManager = new ContactManagerImpl(this);
        });

        initializer.put("consoleXiaomingUser", () -> {
            consoleInputThread = new ConsoleInputThread(this);
            consoleXiaomingUser = new ConsoleXiaomingUserImpl(new ConsoleContactImpl(this, consoleInputThread));

            consoleXiaomingUser.setReceptionist(receptionistManager.getBotReceptionist());
            consoleInputThread.setConsoleUser(consoleXiaomingUser);

            scheduler.run(consoleInputThread).setDescription("控制台输入任务");
        });
    }

    /**
     * 创建一些小明必要的的文件夹
     */
    void makeDirectories() {
        if (!accountDirectory.isDirectory() && !accountDirectory.mkdirs()) {
            final String message = "无法创建账户文件夹：" + accountDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        if (!resourceDirectory.isDirectory() && !resourceDirectory.mkdirs()) {
            final String message = "无法创建本地资源文件夹：" + resourceDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        if (!configurationDirectory.isDirectory() && !configurationDirectory.mkdirs()) {
            final String message = "无法创建配置文件夹：" + configurationDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        if (!pluginDirectory.isDirectory() && !pluginDirectory.mkdirs()) {
            final String message = "无法创建插件文件夹：" + pluginDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        if (!logDirectory.isDirectory() && !logDirectory.mkdirs()) {
            final String message = "无法创建日志文件夹：" + logDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }
    }

    /**
     * 注册内核所需的一些监听器之类
     */
    void registerCoreModules() {
        // 注册内核指令处理器
        // 全局交互器
        interactorManager.register(new GlobalCommandInteractor(this), null);
        if (configuration.isEnablePreviewFunctions()) {
            interactorManager.register(new DebugCommandInterator(this), null);
        }

        interactorManager.register(new PluginInteractor(this), null);
        interactorManager.register(new ResourceCommandInteractor(this), null);
        interactorManager.register(new AccountCommandInteractor(this), null);
        interactorManager.register(new ReportCommandInteractor(this), null);
        interactorManager.register(new CallLimitCommandInteractor(this), null);
        interactorManager.register(new CoreCommandInteractor(this), null);
        interactorManager.register(new ConfigurationCommandInteractor(this), null);
        interactorManager.register(new PermissionCommandInteractor(this), null);
        interactorManager.register(new ResponseGroupCommandInteractor(this), null);
        interactorManager.register(new LanguageCommandIterator(this), null);

        // 注册内核交互器
        interactorManager.register(new ReportInteractor(), null);
        interactorManager.denyCoreRegister();

        // 注册内核监听器
        eventManager.register(receptionistManager, null);
        eventManager.denyCoreRegister();

        // 设置调用限制
        userCallLimitManager.getGroupCallLimiter().setConfig(configuration.getGroupCallConfig());
        userCallLimitManager.getPrivateCallLimiter().setConfig(configuration.getPrivateCallConfig());
    }

    void initialize() {
        makeDirectories();

        fillInitializer();
        load();

        registerCoreModules();

        // 加载所有的插件
        try {
            pluginManager.loadAllPlugins(consoleXiaomingUser);
        } catch (Throwable throwable) {
            getLog().error("加载所有插件时出现异常：", throwable);
        }
    }

    @Override
    public void start() {
        final List<String> tips = Arrays.asList(
                "你知道吗，当你看到这条 TIPS 时，你就阅读了一条 TIPS",
                "@FilterParameter(\"argument\") 除了这种最基础的用法，" +
                        "还可以自定义默认参数值，" +
                        "就像 @FilterParameter(value = \"argument\", defaultValue = \"defaultValue\")",
                "你知道吗，椽子的英文名 Chuanwise 前半部分是拼音，后半部分是英文"
        );

        getLog().warn("\n" +
                "\n" +
                " __   __ _                __  __  _               \n" +
                " \\ \\ / /(_)              |  \\/  |(_)              \n" +
                "  \\ V /  _   __ _   ___  | \\  / | _  _ __    __ _ \n" +
                "   > <  | | / _` | / _ \\ | |\\/| || || '_ \\  / _` |\n" +
                "  / . \\ | || (_| || (_) || |  | || || | | || (_| |\n" +
                " /_/ \\_\\|_| \\__,_| \\___/ |_|  |_||_||_| |_| \\__, |\n" +
                "                                             __/ |\n" +
                "                                            |___/ \n" +
                "                                        @" + SPONSOR + "\n" +
                "core version: " + VERSION + "\n" +
                "github: " + GITHUB + "\n" +
                "tips: " + tips.get(new Random().nextInt(tips.size())) + "\n");
        getLog().info("正在启动小明机器人……");

        if (Objects.isNull(miraiBot)) {
            throw new NoSuchBotException();
        }

        stop = false;
        initialize();

        // 登录机器人
        miraiBot.login();

        // 将 mirai 的事件转发到小明的中央消息处理器
        final EventChannel<BotEvent> eventChannel = miraiBot.getEventChannel();
        eventChannel.registerListenerHost(new ListenerHost() {
            @EventHandler
            public void onEvent(Event event) {
                eventManager.callLater(event);
            }
        });

        try {
            post();
        } catch (Exception exception) {
            getLog().error(exception.getMessage(), exception);
        }
        getLog().info("小明机器人启动完成");
    }

    /**
     * 小明启动后的一些操作
     */
    void post() {
        if (configuration.isEnableStartLog()) {
            responseGroupManager.sendMessageToTaggedGroup("log", "{xiaomingEnabled}");
        }
    }

    /**
     * 统一权限管理器
     */
    @Setter
    File configurationDirectory = PathUtils.CONFIG;
    PermissionManager permissionManager;

    Language language;

    ContactManager contactManager;

    /**
     * 插件管理器
     */
    @Setter
    File pluginDirectory = PathUtils.PLUGIN;
    PluginManager pluginManager;

    /**
     * 交互器管理器
     */
    InteractorManager interactorManager;

    /**
     * 监听器管理器
     */
    EventManager eventManager;

    /**
     * 用户调用限制管理器
     */
    UserCallLimitManager userCallLimitManager;

    /**
     * 小明基本设置
     */
    Configuration configuration;

    /**
     * 小明统计数据
     */
    Statistician statistician;

    /**
     * 机器人正在执行的标记，默认是 {@code true}，需要使用 start 启动
     */
    volatile boolean stop = true;

    /**
     * 控制台小明使用者
     */
    ConsoleInputThread consoleInputThread;
    ConsoleXiaomingUser consoleXiaomingUser;

    /**
     * 用户数据管理器
     */
    @Setter
    File accountDirectory = PathUtils.ACCOUNT;
    AccountManager accountManager;

    /**
     * 响应群管理器
     */
    ResponseGroupManager responseGroupManager;

    /**
     * 用户交互线程管理器
     */
    ReceptionistManager receptionistManager;

    /**
     * 错误记录器
     */
    ReportMessageManager reportMessageManager;

    /**
     * 本地资源管理器
     */
    File resourceDirectory = PathUtils.RESOURCES;
    ResourceManager resourceManager;

    /**
     * 用户须知管理器
     */
    LicenseManager licenseManager;

    /** 调度器 */
    Scheduler scheduler;

    File logDirectory = PathUtils.LOG;

    /** 小明类加载器，用于加载插件及其相关配置文件 */
    XiaomingClassLoader xiaomingClassLoader = new XiaomingClassLoader(getClass().getClassLoader());

    /** 自定义序列化器，用来存储文件等 */
    Serializer serializer;
    {
        serializer = new JackJsonSerializer(SerializerUtility.initialized(new ObjectMapper()));
        serializer.setClassLoader(xiaomingClassLoader);
    }

    /** 核心序列化器，用来存储关键文件。例如 configurations 等不能变更序列化器的文件 */
    final Serializer coreSerializer = serializer;

    /**
     * 文件存储信息载入和读取器
     */
    FileLoader fileLoader = new JsonFileLoader(((JsonSerializer) serializer));

    @Override
    public synchronized void stop() {
        if (isStop()) {
            throw new XiaomingRuntimeException("can not stop a stopped xiaoming bot");
        }

        stop = true;

        // 关闭所有的插件
        getLog().info("正在关闭所有插件");
        try {
            for (XiaomingPlugin plugin : pluginManager.getEnabledPlugins()) {
                getLog().info("正在关闭插件：{}", plugin.getCompleteName());
                try {
                    plugin.onDisable();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (Exception exception) {
            getLog().error(exception.getMessage(), exception);
        }

        getLog().info("正在卸载所有插件");
        try {
            for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
                getLog().info("正在卸载插件：{}", plugin.getCompleteName());
                try {
                    plugin.onUnload();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (Exception exception) {
            getLog().error(exception.getMessage(), exception);
        }

        getLog().info("正在关闭 mirai 机器人");
        try {
            miraiBot.close();
        } catch (Throwable throwable) {
            getLog().error(throwable.getMessage(), throwable);
        }

        getLog().info("正在关闭线程池");

        // 唤醒并关闭所有用户线程
        receptionistManager.close();

        // 添加开关机记录
        statistician.onClose();

        // 给线程池下关闭命令，等待 10 秒后检查是否成功关闭
        scheduler.stop();

        final ExecutorService threadPool = scheduler.getThreadPool();
        // 如果还没关闭就尝试关闭一下
        if (!threadPool.isShutdown()) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ignored) {
            }
            try {
                int remainTryTimes = 5;
                while (!threadPool.awaitTermination(5, TimeUnit.SECONDS) && remainTryTimes > 0) {
                    getLog().warn("线程仍然没有全部结束，请稍等，小明还会尝试 " + remainTryTimes + " 次……");
                    remainTryTimes--;
                }
            } catch (InterruptedException exception) {
                getLog().warn("等待线程池关闭被强行中止");
            }
        }
    }

    @Override
    public void optimize() {
        // 最多连续尝试一分钟
        // 不断尝试直到当前没有任何人正在交互
        long latestTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        while (!receptionistManager.getReceptionists().isEmpty() && System.currentTimeMillis() < latestTime) {
            receptionistManager.optimize();
        }

        // 清空缓存的所有聊天记录
        if (latestTime < System.currentTimeMillis()) {
            contactManager.clear();
        }
        System.gc();
    }
}