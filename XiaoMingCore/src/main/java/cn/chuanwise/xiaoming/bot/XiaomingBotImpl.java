package cn.chuanwise.xiaoming.bot;

import cn.chuanwise.api.SimpleSetableStatusHolder;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.preservable.loader.FileLoader;
import cn.chuanwise.toolkit.preservable.loader.JsonFileLoader;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.toolkit.serialize.serializer.json.JsonSerializer;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StreamUtil;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.ContactManager;
import cn.chuanwise.xiaoming.group.GroupInformationManagerImpl;
import cn.chuanwise.xiaoming.interactor.interactors.*;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.language.LanguageManagerImpl;
import cn.chuanwise.xiaoming.listener.CoreListeners;
import cn.chuanwise.xiaoming.listener.EventManager;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.optimize.Optimizer;
import cn.chuanwise.xiaoming.optimize.OptimizerImpl;
import cn.chuanwise.xiaoming.permission.PermissionService;
import cn.chuanwise.xiaoming.permission.PermissionServiceImpl;
import cn.chuanwise.xiaoming.exception.NoSuchBotException;
import cn.chuanwise.xiaoming.exception.XiaomingInitializeException;
import cn.chuanwise.xiaoming.exception.XiaomingRuntimeException;
import cn.chuanwise.xiaoming.group.GroupInformationManager;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.schedule.Scheduler;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.schedule.SchedulerImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.account.AccountManagerImpl;
import cn.chuanwise.xiaoming.contact.ContactManagerImpl;
import cn.chuanwise.xiaoming.contact.contact.ConsoleContactImpl;
import cn.chuanwise.xiaoming.interactor.InteractorManagerImpl;
import cn.chuanwise.xiaoming.schedule.FileSaverImpl;
import cn.chuanwise.xiaoming.thread.ConsoleInputThread;
import cn.chuanwise.xiaoming.configuration.ConfigurationImpl;
import cn.chuanwise.xiaoming.configuration.StatisticianImpl;
import cn.chuanwise.xiaoming.recept.ReceptionistManagerImpl;
import cn.chuanwise.xiaoming.resource.ResourceManager;
import cn.chuanwise.xiaoming.resource.ResourceManagerImpl;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUserImpl;
import cn.chuanwise.xiaoming.listener.EventManagerImpl;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.plugin.PluginManagerImpl;
import cn.chuanwise.xiaoming.util.LanguageConfigUtil;
import cn.chuanwise.util.SerializerUtil;
import lombok.Getter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
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
    private static final String LOGGER_NAME = "XiaoMingCore";
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
        fillInitializer();
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

        scheduler.runFinally("保存文件", () -> {
            final Map<File, Preservable> preservables = getFileSaver().getPreservables();
            if (preservables.isEmpty()) {
                logger.info("没有任何需要保存的文件");
            } else {
                logger.info("正在保存 " + preservables.size() + " 个文件");
                getFileSaver().save();
                if (preservables.isEmpty()) {
                    logger.info("全部文件保存成功");
                } else {
                    logger.info("这些文件无法保存，小明已经尽力了：\n" +
                            CollectionUtil.toIndexString(preservables.keySet(), File::getAbsolutePath));
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

        checkIfCanDeleteAndLog.accept(new File(workingDirectory, "texts"), "文本文件");
        checkIfCanDeleteAndLog.accept(new File(workingDirectory, "accounts"), "账户文件夹");
        checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "permissions.json"), "权限文件");
        checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "license.json"), "协议文件");
        checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "reports.json"), "错误报告文件");

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

            if (directory.isDirectory()) {
                checkIfCanDeleteAndLog.accept(directory, "语言文件夹");
            }

            languageManager = new LanguageManagerImpl(this);
            checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "language.json"), "语言文件");
            checkIfCanDeleteAndLog.accept(new File(configurationDirectory, "language"), "语言文件夹");

            languageManager.setXiaomingBot(this);
        });

        initializer.put("permissionService", () -> {
            permissionService = new PermissionServiceImpl(this);
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
            accountManager = fileLoader
                    .loadOrSupply(AccountManagerImpl.class, new File(configurationDirectory, "accounts.json"), AccountManagerImpl::new);
            accountManager.setXiaomingBot(this);
        });

        initializer.put("groupInformationManager", () -> {
            final File file = new File(configurationDirectory, "groups.json");
            checkIfExistAndLog.accept(file, "响应群数据文件");

            groupInformationManager = fileLoader
                    .loadOrSupply(GroupInformationManagerImpl.class, file, GroupInformationManagerImpl::new);
            groupInformationManager.setXiaomingBot(this);
        });

        initializer.put("receptionistManager", () -> {
            receptionistManager = new ReceptionistManagerImpl(this);
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

        initializer.put("contactManager", () -> {
            contactManager = new ContactManagerImpl(this);
        });
    }

    /**
     * 创建一些小明必要的的文件夹
     */
    void makeDirectories() {
        configurationDirectory = new File(workingDirectory, "configurations");
        pluginDirectory = new File(workingDirectory, "plugins");
        logDirectory = new File(workingDirectory, "logs");
        resourceDirectory = new File(workingDirectory, "resources");

        if (!resourceDirectory.isDirectory() && !resourceDirectory.mkdirs()) {
            final String message = "无法创建本地资源文件夹：" + resourceDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        if (!reportDirectory.isDirectory() && !reportDirectory.mkdirs()) {
            final String message = "无法创建错误报告文件夹：" + resourceDirectory.getAbsolutePath();
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
//        if (VERSION_TYPE == VersionType.EXPERIMENTAL) {
//            interactorManager.registerInteractors(new ExperimentalInteractors(), null);
//        }
        // 注册内核交互器
        interactorManager.registerInteractors(new PluginInteractors(), null);
        interactorManager.registerInteractors(new ReceptionistInteractors(), null);
        interactorManager.registerInteractors(new ResourceInteractors(), null);
        interactorManager.registerInteractors(new AccountInteractors(), null);
        interactorManager.registerInteractors(new CoreInteractors(), null);
        interactorManager.registerInteractors(new ConfigurationInteractors(), null);
        interactorManager.registerInteractors(new GroupRecordInteractors(), null);
        interactorManager.registerInteractors(new PermissionInteractors(), null);

        // 注册内核监听器
        eventManager.registerListeners(receptionistManager, null);
        eventManager.registerListeners(new CoreListeners(), null);
    }

    private void initialize() {
        makeDirectories();

        load();
        LanguageConfigUtil.config(languageManager);

        consoleInputThread = new ConsoleInputThread(this);
        consoleXiaomingUser = new ConsoleXiaomingUserImpl(new ConsoleContactImpl(this, consoleInputThread));

        consoleXiaomingUser.setReceptionist(receptionistManager.getReceptionist(getCode()));
        consoleInputThread.setUser(consoleXiaomingUser);

        scheduler.run(consoleInputThread);

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
        final List<String> tips = Arrays.asList(
                "你知道吗，当你看到这条 TIPS 时，你就阅读了一条 TIPS",
                "椽子曾经把小明互通插件重写过一次，那次更新花了很长时间",
                "太学最初的全称「明城府联合太学」是椽子在一天下午手里拿着夹馍在教学楼门口突然想到的",
               " 小明曾经不是插件化的，所有功能都在内核里"
        );
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
                "core version: " + XiaomingBot.VERSION + "\n" +
                "github: " + GITHUB + "\n" +
                "tips: " + CollectionUtil.randomGet(tips) + "\n");
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
        getLogger().info("登录 QQ 过程可能卡顿，请耐心等待 ……");
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
            contactManager.sendGroupMessage("log", "小明机器人启动完成");
        }
    }

    File workingDirectory = new File(System.getProperty("user.dir"));

    File reportDirectory = new File(workingDirectory, "reports");

    /**
     * 统一权限管理器
     */
    File configurationDirectory;
    PermissionService permissionService;

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
    AccountManager accountManager;

    /**
     * 响应群管理器
     */
    GroupInformationManager groupInformationManager;

    /**
     * 用户交互线程管理器
     */
    ReceptionistManager receptionistManager;

    /**
     * 本地资源管理器
     */
    File resourceDirectory;
    ResourceManager resourceManager;

    /** 调度器 */
    Scheduler scheduler;

    File logDirectory;

    /** 小明类加载器，用于加载插件及其相关配置文件 */
    XiaomingClassLoader xiaomingClassLoader = new XiaomingClassLoader(getClass().getClassLoader());

    /** 自定义序列化器，用来存储文件等 */
    Serializer serializer = SerializerUtil.initializedSerializer();
    {
        serializer.getConfiguration().getClassLoader().addClassLoader(xiaomingClassLoader);
    }

    /** 核心序列化器，用来存储关键文件。例如 configurations 等不能变更序列化器的文件 */
    final Serializer coreSerializer = SerializerUtil.initializedSerializer();
    {
        coreSerializer.getConfiguration().getClassLoader().addClassLoader(xiaomingClassLoader);
    }

    /** 核心文件载入器 */
    FileLoader coreFileLoader = new JsonFileLoader(((JsonSerializer) coreSerializer)) {
        @Override
        public <T extends Preservable> T load(Serializer serializer, Class<T> clazz, File file) throws IOException {
            final T result = super.load(serializer, clazz, file);
            if (result instanceof XiaomingObject) {
                ((XiaomingObject) result).setXiaomingBot(XiaomingBotImpl.this);
            }

            return result;
        }

        @Override
        public <T extends Preservable> T loadOrSupply(Serializer serializer, Class<T> clazz, File file, Supplier<T> supplier) {
            final boolean shouldSave = !configuration.isDelayWrite() && !file.isFile();
            final T t = super.loadOrSupply(serializer, clazz, file, supplier);
            if (shouldSave) {
                try {
                    t.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return t;
        }
    };
    {
        coreFileLoader.setDecodingCharset(StandardCharsets.UTF_8);
    }

    /** 文件存储信息载入和读取器 */
    FileLoader fileLoader = new JsonFileLoader(((JsonSerializer) serializer)) {
        @Override
        public <T extends Preservable> T load(Serializer serializer, Class<T> clazz, File file) throws IOException {
            final T result = super.load(serializer, clazz, file);
            if (result instanceof XiaomingObject) {
                ((XiaomingObject) result).setXiaomingBot(XiaomingBotImpl.this);
            }

            return result;
        }

        @Override
        public <T extends Preservable> T loadOrSupply(Serializer serializer, Class<T> clazz, File file, Supplier<T> supplier) {
            final boolean shouldSave = !configuration.isDelayWrite() && !file.isFile();
            final T t = super.loadOrSupply(serializer, clazz, file, supplier);
            if (shouldSave) {
                try {
                    t.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return t;
        }
    };
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

        fileSaver.readyToSave(accountManager);
        fileSaver.readyToSave(groupInformationManager);
        fileSaver.readyToSave(configuration);
        fileSaver.readyToSave(statistician);

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

        // 如果正在输入，打断
        Optional.ofNullable(consoleInputThread.getThread())
                .ifPresent(Thread::interrupt);

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