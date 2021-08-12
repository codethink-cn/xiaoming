package cn.chuanwise.xiaoming.bot;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.preservable.file.FileLoader;
import cn.chuanwise.toolkit.preservable.file.loader.JsonFileLoader;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.toolkit.serialize.serializer.json.JackJsonSerializer;
import cn.chuanwise.toolkit.serialize.serializer.json.JsonSerializer;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.FileUtility;
import cn.chuanwise.utility.ResourceUtility;
import cn.chuanwise.utility.StreamUtility;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.ContactManager;
import cn.chuanwise.xiaoming.interactor.core.*;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.LanguageManagerImpl;
import cn.chuanwise.xiaoming.listener.CoreListener;
import cn.chuanwise.xiaoming.optimize.Optimizer;
import cn.chuanwise.xiaoming.optimize.OptimizerImpl;
import cn.chuanwise.xiaoming.report.ReportMessageManager;
import cn.chuanwise.xiaoming.exception.NoSuchBotException;
import cn.chuanwise.xiaoming.exception.XiaomingInitializeException;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.group.GroupRecordManager;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.schedule.Scheduler;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.schedule.SchedulerImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.utility.LanguageUtility;
import cn.chuanwise.xiaoming.utility.PathUtility;
import cn.chuanwise.xiaoming.event.EventManager;
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
import cn.chuanwise.xiaoming.event.EventManagerImpl;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.limit.UserCallLimitManagerImpl;
import cn.chuanwise.xiaoming.permission.PermissionManagerImpl;
import cn.chuanwise.xiaoming.plugin.PluginManagerImpl;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * 小明机器人核心
 * @author Chuanwise
 */
@NoArgsConstructor
@Getter
@Setter
public class XiaomingBotImpl implements XiaomingBot {
    Logger logger = LoggerFactory.getLogger(getClass());

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

        scheduler.runFinally(() -> {
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
                    "base\n" +
                    "configuration\n" +
                    "core\n" +
                    "group\n" +
                    "license\n" +
                    "permission\n" +
                    "resource").split(Pattern.quote("\n"));

            for (String languageFileName : languageFileNames) {
                final File languageFile = new File(directory, languageFileName + ".json");
                final String resourcePath = "languages/" + languageFileName + ".json";

                try {
                    logger.info("正在检查更新语言文件：" + languageFile);
                    LanguageUtility.loadOrCopy(this, languageFile, xiaomingClassLoader, resourcePath);
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

        initializer.put("consoleXiaomingUser", () -> {
            consoleInputThread = new ConsoleInputThread(this);
            consoleXiaomingUser = new ConsoleXiaomingUserImpl(new ConsoleContactImpl(this, consoleInputThread));

            consoleXiaomingUser.setReceptionist(receptionistManager.getBotReceptionist());
            consoleInputThread.setConsoleUser(consoleXiaomingUser);

            scheduler.run(consoleInputThread);
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
        interactorManager.register(new GlobalInteractor(this), null);

        if (configuration.isEnablePreviewFunctions()) {
            interactorManager.register(new PreviewFunctionInteractor(this), null);
        }

        if (configuration.isDebug()) {
            interactorManager.register(new DebugInteractor(this), null);
        }

        // 注册内核交互器
        interactorManager.register(new PluginInteractor(this), null);
        interactorManager.register(new ResourceInteractor(this), null);
        interactorManager.register(new AccountInteractor(this), null);
        interactorManager.register(new ReportInteractor(this), null);
        interactorManager.register(new CallLimitInteractor(this), null);
        interactorManager.register(new CoreInteractor(this), null);
        interactorManager.register(new ConfigurationInteractor(this), null);
        interactorManager.register(new PermissionInteractor(this), null);
        interactorManager.register(new GroupRecordInteractor(this), null);
        interactorManager.register(new LanguageIterator(this), null);
        interactorManager.denyCoreRegister();

        // 注册内核监听器
        eventManager.register(receptionistManager, null);
        eventManager.register(new CoreListener(), null);
        eventManager.denyCoreRegister();

        // 设置调用限制
        userCallLimitManager.getGroupCallLimiter().setConfiguration(configuration.getGroupCallConfig());
        userCallLimitManager.getPrivateCallLimiter().setConfiguration(configuration.getPrivateCallConfig());
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
            this.getLogger().error("加载所有插件时出现异常：", throwable);
        }
    }

    @Override
    public void start() {
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

        this.getLogger().warn("\n" +
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
        this.getLogger().info("正在启动小明机器人……");

        if (Objects.isNull(miraiBot)) {
            throw new NoSuchBotException();
        }

        // 翻译参数
        final String property = System.getProperty("xiaoming.slider.captcha.supported");
        if (Objects.nonNull(property)) {
            System.setProperty("mirai.slider.captcha.supported", "");
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
                eventManager.callEventAsync(event);
            }
        });

        try {
            post();
        } catch (Exception exception) {
            this.getLogger().error(exception.getMessage(), exception);
        }
        this.getLogger().info("小明机器人启动完成");
    }

    /**
     * 小明启动后的一些操作
     */
    void post() {
        if (configuration.isEnableStartLog()) {
            contactManager.sendGroupMessage("log", "{lang.xiaomingEnabled}");
        }
    }

    /**
     * 统一权限管理器
     */
    File configurationDirectory = PathUtility.CONFIG;
    PermissionManager permissionManager;

    LanguageManager languageManager;

    ContactManager contactManager;

    /**
     * 插件管理器
     */
    File pluginDirectory = PathUtility.PLUGIN;
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
    File accountDirectory = PathUtility.ACCOUNT;
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
    File resourceDirectory = PathUtility.RESOURCES;
    ResourceManager resourceManager;

    /** 用户须知管理器 */
    LicenseManager licenseManager;

    /** 调度器 */
    Scheduler scheduler;

    File logDirectory = PathUtility.LOG;

    /** 小明类加载器，用于加载插件及其相关配置文件 */
    XiaomingClassLoader xiaomingClassLoader = new XiaomingClassLoader(getClass().getClassLoader());

    private Serializer initializedSerializer() {
        final ObjectMapper objectMapper = new ObjectMapper();

        // 只使用公开的 setter
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        // 不使用 getter
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        // 直接填充 field
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 序列化不明确的类时，写上类名
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS, JsonTypeInfo.As.PROPERTY);

        final Serializer serializer = new JackJsonSerializer(objectMapper);
        serializer.getConfiguration().getClassLoader().addClassLoader(xiaomingClassLoader);

        return serializer;
    }

    /** 自定义序列化器，用来存储文件等 */
    Serializer serializer = initializedSerializer();

    /** 核心序列化器，用来存储关键文件。例如 configurations 等不能变更序列化器的文件 */
    final Serializer coreSerializer = initializedSerializer();

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
        if (isStop()) {
            throw new XiaomingRuntimeException("can not stop a stopped xiaoming bot");
        }

        stop = true;

        // 关闭所有的插件
        this.getLogger().info("正在关闭所有插件");
        try {
            for (XiaomingPlugin plugin : pluginManager.getEnabledPlugins()) {
                this.getLogger().info("正在关闭插件：{}", plugin.getCompleteName());
                try {
                    plugin.onDisable();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (Exception exception) {
            this.getLogger().error(exception.getMessage(), exception);
        }

        this.getLogger().info("正在卸载所有插件");
        try {
            for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
                this.getLogger().info("正在卸载插件：{}", plugin.getCompleteName());
                try {
                    plugin.onUnload();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (Exception exception) {
            this.getLogger().error(exception.getMessage(), exception);
        }

        this.getLogger().info("正在关闭 mirai 机器人");
        try {
            miraiBot.close();
        } catch (Throwable throwable) {
            this.getLogger().error(throwable.getMessage(), throwable);
        }

        this.getLogger().info("正在关闭线程池");

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
                    this.getLogger().warn("线程仍然没有全部结束，请稍等，小明还会尝试 " + remainTryTimes + " 次……");
                    remainTryTimes--;
                }
            } catch (InterruptedException exception) {
                this.getLogger().warn("等待线程池关闭被强行中止");
            }
        }
    }
}