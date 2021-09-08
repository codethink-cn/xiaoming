package cn.chuanwise.xiaoming.bot;

import cn.chuanwise.api.SimpleSetableStatusHolder;
import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.preservable.file.FileLoader;
import cn.chuanwise.toolkit.preservable.file.loader.JsonFileLoader;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.toolkit.serialize.serializer.json.JsonSerializer;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.FunctionalUtility;
import cn.chuanwise.utility.ObjectUtility;
import cn.chuanwise.utility.StreamUtility;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.client.CenterClient;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.ContactManager;
import cn.chuanwise.xiaoming.interactor.core.*;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.LanguageManagerImpl;
import cn.chuanwise.xiaoming.listener.CoreListener;
import cn.chuanwise.xiaoming.listener.EventManager;
import cn.chuanwise.xiaoming.optimize.Optimizer;
import cn.chuanwise.xiaoming.optimize.OptimizerImpl;
import cn.chuanwise.xiaoming.report.ReportMessageManager;
import cn.chuanwise.xiaoming.exception.NoSuchBotException;
import cn.chuanwise.xiaoming.exception.XiaomingInitializeException;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.group.GroupRecordManager;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.schedule.Scheduler;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.schedule.SchedulerImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.limit.UserCallLimitManager;
import cn.chuanwise.xiaoming.permission.PermissionManager;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.account.AccountManagerImpl;
import cn.chuanwise.xiaoming.contact.ContactManagerImpl;
import cn.chuanwise.xiaoming.contact.contact.ConsoleContactImpl;
import cn.chuanwise.xiaoming.report.ReportMessageManagerImpl;
import cn.chuanwise.xiaoming.interactor.InteractorManagerImpl;
import cn.chuanwise.xiaoming.license.LicenceManagerImpl;
import cn.chuanwise.xiaoming.group.GroupRecordManagerImpl;
import cn.chuanwise.xiaoming.schedule.FileSaverImpl;
import cn.chuanwise.xiaoming.thread.ConsoleInputThread;
import cn.chuanwise.xiaoming.configuration.ConfigurationImpl;
import cn.chuanwise.xiaoming.configuration.StatisticianImpl;
import cn.chuanwise.xiaoming.recept.ReceptionistManagerImpl;
import cn.chuanwise.xiaoming.resource.ResourceManager;
import cn.chuanwise.xiaoming.resource.ResourceManagerImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUserImpl;
import cn.chuanwise.xiaoming.language.LanguageImpl;
import cn.chuanwise.xiaoming.listener.EventManagerImpl;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.limit.UserCallLimitManagerImpl;
import cn.chuanwise.xiaoming.permission.PermissionManagerImpl;
import cn.chuanwise.xiaoming.plugin.PluginManagerImpl;
import cn.chuanwise.xiaoming.utility.LanguageUtility;
import cn.chuanwise.xiaoming.utility.SerializerUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * 小明机器人核心
 * @author Chuanwise
 */
@Getter
@Setter
public class XiaomingBotImpl
        extends SimpleSetableStatusHolder<XiaomingBot.Status>
        implements XiaomingBot {
    private static final String LOGGER_NAME = "xiaoming-core";
    Logger logger = LoggerFactory.getLogger(LOGGER_NAME);

    /** mirai 机器人引用 */
    Bot miraiBot;

    public XiaomingBotImpl(Bot miraiBot) {
        super(Status.DISABLED);
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
        final List<Future<?>> futures = new ArrayList<>(initializer.size());
        initializer.values().forEach(runnable -> futures.add(scheduler.run(runnable, null)));
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
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

        // 先用 UTF-8 加载配置文件
        configuration = fileLoader.loadOrSupply(ConfigurationImpl.class, new File(configurationDirectory, "configurations.json"), ConfigurationImpl::new);
        configuration.setXiaomingBot(this);

        // 根据配置文件设置编码
        fileSaver.setEncode(configuration.getStorageEncoding());
        fileLoader.setDecoding(configuration.getStorageDecoding());

        // 马上修改序列化器
        // serializer.setConfiguration(configuration.getSerializerConfiguration());

        // 加载主线程池
        scheduler = new SchedulerImpl(this);

        // 添加自动任务
        scheduler.runAtFixedRateLater(configuration.getOptimizePeriod(), configuration.getOptimizePeriod(), () -> getOptimizer().optimize());
        scheduler.runAtFixedRateLater(configuration.getSavePeriod(), configuration.getSavePeriod(), () -> getFileSaver().save());

        scheduler.runFinally("保存文件", () -> {
            final Map<File, Preservable<File>> preservables = getFileSaver().getPreservables();
            if (preservables.isEmpty()) {
                logger.info("没有任何需要保存的文件");
            } else {
                logger.info("正在保存 " + preservables.size() + " 个文件");
                getFileSaver().save();
                if (preservables.isEmpty()) {
                    logger.info("全部文件保存成功");
                } else {
                    logger.info("这些文件无法保存，小明已经尽力了：\n" +
                            CollectionUtility.toIndexString(preservables.keySet(), File::getAbsolutePath));
                }
            }
        });

        BiConsumer<File, String> checkIfExistAndLog = (file, type) -> {
            if (file.isFile()) {
                logger.info("存在" + type + "：" + file.getAbsolutePath() + "，正在载入");
            } else {
                logger.info("找不到" + type + "：" + file.getAbsolutePath() + "，将使用默认设置");
            }
        };
        BiConsumer<File, String> checkIfCanDeleteAndLog = (file, type) -> {
            if (file.isFile()) {
                logger.warn(file.getAbsolutePath() + "，是早些内核版本的" + type + "，可以删除");
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

        initializer.put("languageManager", () -> {
            final File directory = new File(configurationDirectory, "languages");
            directory.mkdirs();

            languageManager = new LanguageManagerImpl(this, directory);
            checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "language.json"), "语言文件");
            checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "language"), "语言文件夹");

            final String[] languageFileNames = ("account\n" +
                    "apply\n" +
                    "base\n" +
                    "client\n" +
                    "configuration\n" +
                    "core\n" +
                    "group\n" +
                    "interact\n" +
                    "license\n" +
                    "permission\n" +
                    "plugin\n" +
                    "report\n" +
                    "resource").split(Pattern.quote("\n"));

            for (String languageFileName : languageFileNames) {
                final File languageFile = new File(directory, languageFileName + ".json");
                final String resourcePath = "languages/" + languageFileName + ".json";

                try {
                    logger.info("正在检查更新语言文件：" + languageFile);
                    if (Objects.nonNull(xiaomingClassLoader.getResourceAsStream(resourcePath))) {
                        LanguageUtility.loadOrCopy(this, languageFile, xiaomingClassLoader, resourcePath);
                    } else {
                        logger.error("无法找到语言资源文件：" + resourcePath + "（如果正在使用小明调试器，请忽略此提示）");
                    }
                } catch (IOException exception) {
                    logger.info("更新语言文件 " + languageFileName + " 时出现异常", exception);
                }
            }

            // 载入所有的语言文件
            for (File file : directory.listFiles()) {
                final Language language = fileLoader.loadOrFail(LanguageImpl.class, file);
                if (Objects.isNull(language)) {
                    logger.error("载入语言文件 " + file + " 错误");
                } else {
                    languageManager.registerLanguage(language, null);
                }
            }

            languageManager.setXiaomingBot(this);
            languageManager.registerLanguage(fileLoader.loadOrSupply(LanguageImpl.class, directory, LanguageImpl::new), null);
        });

        initializer.put("centerClientManager", () -> {
            centerClient = new CenterClient(this);
        });

        initializer.put("permissionManager", () -> {
            final File file = new File(configurationDirectory, "permissions.json");
            checkIfExistAndLog.accept(file, "权限组文件");

            permissionManager = fileLoader
                    .loadOrSupply(PermissionManagerImpl.class, file, PermissionManagerImpl::new);
            permissionManager.setXiaomingBot(this);
        });

        initializer.put("statistician", () -> {
            final File file = new File(configurationDirectory, "statisticians.json");
            final String fileType = "统计数据文件";
            checkIfExistAndLog.accept(file, fileType);

            final File elderVersionFile = new File(configurationDirectory, "counters.json");
            checkIfCanDeleteAndLog.accept(elderVersionFile, fileType);

            statistician = fileLoader.loadOrSupply(StatisticianImpl.class, file, StatisticianImpl::new);
            statistician.setXiaomingBot(this);
        });

        initializer.put("accountManager", () -> {
            accountManager = new AccountManagerImpl(this, accountDirectory);
        });

        initializer.put("groupRecordManager", () -> {
            final File file = new File(configurationDirectory, "groups.json");
            checkIfExistAndLog.accept(file, "响应群数据文件");

            groupRecordManager = fileLoader
                    .loadOrSupply(GroupRecordManagerImpl.class, file, GroupRecordManagerImpl::new);
            groupRecordManager.setXiaomingBot(this);
            ((GroupRecordManagerImpl) groupRecordManager).setGroups(((Set) groupRecordManager.getGroups()));
        });

        initializer.put("receptionistManager", () -> {
            receptionistManager = new ReceptionistManagerImpl(this);
        });

        initializer.put("reportMessageManager", () -> {
            final File file = new File(configurationDirectory, "reports.json");
            checkIfExistAndLog.accept(file, "反馈和错误报告文件");

            reportMessageManager = fileLoader
                    .loadOrSupply(ReportMessageManagerImpl.class, file, ReportMessageManagerImpl::new);
            reportMessageManager.setXiaomingBot(this);
        });

        initializer.put("resourceManager", () -> {
            final File file = new File(resourceDirectory, "resources.json");
            checkIfExistAndLog.accept(file, "资源概况文件");

            resourceManager = fileLoader
                    .loadOrSupply(ResourceManagerImpl.class, file, ResourceManagerImpl::new);
            resourceManager.setXiaomingBot(this);
            resourceManager.setResourceDirectory(resourceDirectory);
            resourceManager.flushBotReference(this);
        });

        initializer.put("licenseManager", () -> {
            final File file = new File(configurationDirectory, "license.json");
            checkIfExistAndLog.accept(file, "小明协议验证数据");

            licenseManager = fileLoader
                    .loadOrSupply(LicenceManagerImpl.class, file, LicenceManagerImpl::new);
            licenseManager.setXiaomingBot(this);
        });

        initializer.put("contactManager", () -> {
            contactManager = new ContactManagerImpl(this);
        });

        initializer.put("console", () -> {
            consoleInputThread = new ConsoleInputThread(this);
            consoleXiaomingUser = new ConsoleXiaomingUserImpl(new ConsoleContactImpl(this, consoleInputThread));

            consoleXiaomingUser.setReceptionist(receptionistManager.getReceptionist(0));
            consoleInputThread.setUser(consoleXiaomingUser);

            scheduler.run(consoleInputThread);
        });
    }

    /**
     * 创建一些小明必要的的文件夹
     */
    void makeDirectories() {
        accountDirectory = new File(workingDirectory, "accounts");
        configurationDirectory = new File(workingDirectory, "configurations");
        pluginDirectory = new File(workingDirectory, "plugins");
        logDirectory = new File(workingDirectory, "logs");
        resourceDirectory = new File(workingDirectory, "resources");

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
        interactorManager.registerInteractors(new GlobalInteractors(), null);

        if (configuration.isEnablePreviewFunctions()) {
            interactorManager.registerInteractors(new PreviewFunctionInteractors(), null);
        }
//        if (configuration.isDebug()) {
            interactorManager.registerInteractors(new DebugInteractors(), null);
//        }

        // 注册内核交互器
        interactorManager.registerInteractors(new PluginInteractors(), null);
        interactorManager.registerInteractors(new ResourceInteractors(), null);
        interactorManager.registerInteractors(new AccountInteractors(), null);
        interactorManager.registerInteractors(new ReportInteractors(), null);
        interactorManager.registerInteractors(new CallLimitInteractors(), null);
        interactorManager.registerInteractors(new CoreInteractors(), null);
        interactorManager.registerInteractors(new CenterInteractors(), null);
        interactorManager.registerInteractors(new ConfigurationInteractors(), null);
        interactorManager.registerInteractors(new PermissionInteractors(), null);
        interactorManager.registerInteractors(new GroupRecordInteractors(), null);
        interactorManager.registerInteractors(new LanguageIterator(), null);
        interactorManager.registerInteractors(new ApplyInteractors(), null);

        // 注册内核监听器
        eventManager.registerListeners(receptionistManager, null);
        eventManager.registerListeners(new CoreListener(), null);

        // 设置调用限制
        userCallLimitManager.getGroupCallLimiter().setConfiguration(configuration.getGroupCallConfig());
        userCallLimitManager.getPrivateCallLimiter().setConfiguration(configuration.getPrivateCallConfig());
    }

    private void initialize() {
        makeDirectories();

        fillInitializer();
        load();

        registerCoreModules();

        // 加载所有的插件
        try {
            pluginManager.initialize();
        } catch (Throwable throwable) {
            getLogger().error("加载所有插件时出现异常：", throwable);
        }
    }

    private List<String> loadTips() {
        // 载入启动时的小 tips
        List<String> tips = null;
        final InputStream tipStream = getClass().getClassLoader().getResourceAsStream("tips.txt");
        if (Objects.nonNull(tipStream)) {
            try {
                final String[] tipsArray = new String(StreamUtility.read(tipStream)).split(Pattern.quote("\n"));
                if (tipsArray.length > 0) {
                    tips = Arrays.asList(tipsArray);
                }
            } catch (IOException ignored) {
            }
        }
        if (CollectionUtility.isEmpty(tips)) {
            tips = Arrays.asList("你知道吗，当你看到这条 TIPS 时，你就阅读了一条 TIPS");
        }
        return tips;
    }

    private void printIcon() {
        final List<String> tips = loadTips();
        getLogger().warn("\n" +
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
                "core version: " + XiaomingBot.COMPLETE_VERSION + "\n" +
                "github: " + GITHUB + "\n" +
                "tips: " + CollectionUtility.randomGet(tips) + "\n");
    }

    private void translateArguments() {
        // 翻译参数
        final String property = System.getProperty("xiaoming.slider.captcha.supported");
        if (Objects.nonNull(property)) {
            System.setProperty("mirai.slider.captcha.supported", "");
        }
    }

    @Override
    public void start() {
        setStatus(Status.ENABLING);
        printIcon();
        if (Objects.isNull(miraiBot)) {
            throw new NoSuchBotException();
        }

        getLogger().info("正在启动小明机器人……");
        translateArguments();

        // 登录机器人
        miraiBot.login();

        initialize();

        // 将 mirai 的事件转发到小明的中央消息处理器
        final EventChannel<BotEvent> eventChannel = miraiBot.getEventChannel();
        eventChannel.registerListenerHost(new ListenerHost() {
            @EventHandler
            public void onEvent(Event event) {
                eventManager.callEventAsync(event);
            }
        });

        try {
            post();
        } catch (Exception exception) {
            getLogger().error(exception.getMessage(), exception);
        }
        getLogger().info("小明机器人启动完成");
        setStatus(Status.ENABLED);
    }

    /**
     * 小明启动后的一些操作
     */
    void post() {
        if (configuration.isEnableStartLog()) {
            contactManager.sendGroupMessage("log", "{lang.xiaomingEnabled}");
        }
    }

    File workingDirectory = new File(System.getProperty("user.dir"));

    /**
     * 统一权限管理器
     */
    File configurationDirectory;
    PermissionManager permissionManager;

    LanguageManager languageManager;

    ContactManager contactManager;

    /**
     * 插件管理器
     */
    File pluginDirectory;
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

    /** 中央服务器客户端 */
    CenterClient centerClient;

    /**
     * 小明基本设置
     */
    Configuration configuration;

    /**
     * 小明统计数据
     */
    Statistician statistician;

    /**
     * 控制台小明使用者
     */
    ConsoleInputThread consoleInputThread;
    ConsoleXiaomingUser consoleXiaomingUser;

    /**
     * 用户数据管理器
     */
    File accountDirectory;
    AccountManager accountManager;

    /**
     * 响应群管理器
     */
    GroupRecordManager groupRecordManager;

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
    File resourceDirectory;
    ResourceManager resourceManager;

    /** 用户须知管理器 */
    LicenseManager licenseManager;

    /** 调度器 */
    Scheduler scheduler;

    File logDirectory;

    /** 小明类加载器，用于加载插件及其相关配置文件 */
    XiaomingClassLoader xiaomingClassLoader = new XiaomingClassLoader(getClass().getClassLoader());

    /** 自定义序列化器，用来存储文件等 */
    Serializer serializer = SerializerUtility.initializedSerializer();
    {
        serializer.getConfiguration().getClassLoader().addClassLoader(xiaomingClassLoader);
    }

    /** 核心序列化器，用来存储关键文件。例如 configurations 等不能变更序列化器的文件 */
    final Serializer coreSerializer = SerializerUtility.initializedSerializer();
    {
        coreSerializer.getConfiguration().getClassLoader().addClassLoader(xiaomingClassLoader);
    }

    /** 核心文件载入器 */
    FileLoader coreFileLoader = new JsonFileLoader(((JsonSerializer) coreSerializer));
    {
        coreFileLoader.setDecodingCharset(StandardCharsets.UTF_8);
    }

    /** 文件存储信息载入和读取器 */
    FileLoader fileLoader = new JsonFileLoader(((JsonSerializer) serializer));
    {
        fileLoader.setDecodingCharset(StandardCharsets.UTF_8);
    }

    /** 文件保存器 */
    FileSaver fileSaver = new FileSaverImpl(this);
    {
        fileSaver.setEncodeCharset(StandardCharsets.UTF_8);
    }

    /** 性能优化器 */
    Optimizer optimizer = new OptimizerImpl(this);

    @Override
    public synchronized void stop() {
        if (isDisabled()) {
            throw new XiaomingRuntimeException("can not stop a stopped xiaoming bot");
        }

        setStatus(Status.DISABLING);

        getLogger().info("正在卸载所有插件");
        try {
            // 卸载所有插件
            pluginManager.getPluginHandlers().forEach(pluginManager::unloadPlugin);
        } catch (Exception exception) {
            getLogger().error("卸载所有插件时出现异常", exception);
        }

        getLogger().info("正在关闭 mirai 机器人");
        try {
            miraiBot.close();
        } catch (Throwable throwable) {
            getLogger().error(throwable.getMessage(), throwable);
        }

        getLogger().info("正在关闭线程池");

        // 添加小明开关机记录
        statistician.onClose();

        // 关闭中心服务器
        if (centerClient.isConnected()) {
            try {
                getLogger().info("正在断开和小明中心服务器的连接");
                centerClient.disconnectManually();
            } catch (Exception exception) {
                getLogger().info("断开和小明中心服务器的连接时出现异常", exception);
            }
        }

        // 如果正在输入，打断
        FunctionalUtility.runIfArgumentNonNull(Thread::interrupt, consoleInputThread.getThread());

        // 给线程池下关闭命令，等待 10 秒后检查是否成功关闭
        scheduler.stopNow();

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
                    getLogger().warn("线程仍然没有全部结束，请稍等，小明还会尝试 " + remainTryTimes + " 次……");
                    remainTryTimes--;
                }
            } catch (InterruptedException exception) {
                getLogger().warn("等待线程池关闭被强行中止");
            }
        }

        setStatus(Status.DISABLED);
        getLogger().info("成功关闭小明，期待和你的下一次重逢 (๑•̀ㅂ•́)و✧");
    }
}