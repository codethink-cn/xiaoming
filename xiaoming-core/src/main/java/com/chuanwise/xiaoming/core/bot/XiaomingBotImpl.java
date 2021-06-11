package com.chuanwise.xiaoming.core.bot;

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
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.api.util.PathUtils;
import com.chuanwise.xiaoming.api.util.PluginLoaderUtils;
import com.chuanwise.xiaoming.api.util.TimeUtils;
import com.chuanwise.xiaoming.api.language.LanguageManager;
import com.chuanwise.xiaoming.api.event.EventManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
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
import com.chuanwise.xiaoming.core.config.ConfigurationImpl;
import com.chuanwise.xiaoming.core.config.StatisticianImpl;
import com.chuanwise.xiaoming.core.schedule.SchedulerImpl;
import com.chuanwise.xiaoming.core.recept.ReceptionistManagerImpl;
import com.chuanwise.xiaoming.api.resource.ResourceManager;
import com.chuanwise.xiaoming.core.resource.ResourceManagerImpl;
import com.chuanwise.xiaoming.core.user.ConsoleXiaomingUserImpl;
import com.chuanwise.xiaoming.core.language.LanguageManagerImpl;
import com.chuanwise.xiaoming.core.event.EventManagerImpl;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.core.limit.UserCallLimitManagerImpl;
import com.chuanwise.xiaoming.core.permission.PermissionManagerImpl;
import com.chuanwise.xiaoming.core.plugin.PluginManagerImpl;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservableFactory;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
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
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 小明机器人核心
 * @author Chuanwise
 */
@NoArgsConstructor
@Getter
@Slf4j
public class XiaomingBotImpl implements XiaomingBot {
    public static final String VERSION = "1.1";
    public static final String AUTHOR = "Chuanwise";
    public static final String GITHUB = "https://github.com/Chuanwise/xiaoming-bot";

    long lastStartTime = 0;

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

    protected void fillInitializer() {
        initializer.clear();

        // 优先加载配置文件
        configuration = filePreservableFactory
                .loadOrProduce(ConfigurationImpl.class, new File(configDirectory, "configurations.json"), ConfigurationImpl::new);
        configuration.setXiaomingBot(this);

        // 加载调度器
        scheduler = filePreservableFactory
                .loadOrProduce(SchedulerImpl.class, new File(configDirectory, "scheduler.json"), SchedulerImpl::new);
        scheduler.setXiaomingBot(this);
        scheduler.start();

        scheduler.run(consoleInputThread);

        // 添加自动任务
        // 自动优化性能任务
        scheduler.periodicRunLater(this::optimize, configuration.getOptimizePeriod(), configuration.getOptimizePeriod());

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
            if (pluginDirectory.isDirectory()) {
                for (File file : pluginDirectory.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        try {
                            PluginLoaderUtils.extendURLClassLoader(file, ((URLClassLoader) getClass().getClassLoader()));
                        } catch (Exception exception) {
                            getLog().error("无法扩展类加载器", exception);
                        }
                    }
                }
            }
        });

        initializer.put("languageManager", () -> {
            languageManager = filePreservableFactory
                    .loadOrProduce(LanguageManagerImpl.class, new File(configDirectory, "language.json"), LanguageManagerImpl::new);
            languageManager.setXiaomingBot(this);
        });

        initializer.put("permissionManager", () -> {
            permissionManager = filePreservableFactory
                    .loadOrProduce(PermissionManagerImpl.class, new File(configDirectory, "permissions.json"), () -> {
                        PermissionManagerImpl manager = new PermissionManagerImpl();
                        manager.setGroups(new HashMap<>());
                        return manager;
                    });
            permissionManager.setXiaomingBot(this);
        });

        initializer.put("statistician", () -> {
            statistician = filePreservableFactory
                    .loadOrProduce(StatisticianImpl.class, new File(configDirectory, "counters.json"), StatisticianImpl::new);
            statistician.setXiaomingBot(this);
        });

        initializer.put("accountManager", () -> {
            accountManager = new AccountManagerImpl(this, accountDirectory);
        });

        initializer.put("responseGroupManager", () -> {
            responseGroupManager = filePreservableFactory
                    .loadOrProduce(ResponseGroupManagerImpl.class, new File(configDirectory, "groups.json"), ResponseGroupManagerImpl::new);
            responseGroupManager.setXiaomingBot(this);
        });

        initializer.put("receptionistManager", () -> {
            receptionistManager = new ReceptionistManagerImpl(this);
        });

        initializer.put("reportMessageManager", () -> {
            reportMessageManager = filePreservableFactory
                    .loadOrProduce(ReportMessageManagerImpl.class, new File(configDirectory, "errors.json"), ReportMessageManagerImpl::new);
            reportMessageManager.setXiaomingBot(this);
        });

        initializer.put("resourceManager", () -> {
            resourceManager = filePreservableFactory
                    .loadOrProduce(ResourceManagerImpl.class, new File(resourceDirectory, "resources.json"), ResourceManagerImpl::new);
            resourceManager.setXiaomingBot(this);
            resourceManager.setResourceDirectory(resourceDirectory);
        });

        initializer.put("licenseManager", () -> {
            licenseManager = filePreservableFactory
                    .loadOrProduce(LicenceManagerImpl.class, new File(configDirectory, "license.json"), LicenceManagerImpl::new);
            licenseManager.setXiaomingBot(this);
        });

        initializer.put("contactManager", () -> {
            contactManager = new ContactManagerImpl(this);
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

        if (!configDirectory.isDirectory() && !configDirectory.mkdirs()) {
            final String message = "无法创建配置文件夹：" + configDirectory.getAbsolutePath();
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

        File lastestLog = new File(logDirectory, "lastest.log");
        if (lastestLog.isFile()) {
            final File dest = new File(logDirectory, TimeUtils.FORMAT.format(lastestLog.lastModified()) + ".log");
            lastestLog.renameTo(dest);
        }
        try {
            if (!lastestLog.isFile()) {
                lastestLog.createNewFile();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            final String message = "无法创建日志文件 " + logDirectory.getAbsolutePath() + " 时出现异常：" + exception;
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
        interactorManager.register(new DebugCommandInterator(this), null);

        interactorManager.register(new PluginInteractor(this), null);
        interactorManager.register(new ResourceCommandInteractor(this), null);
        interactorManager.register(new SchedulerCommandInteractor(), null);
        interactorManager.register(new AccountCommandInteractor(this), null);
        interactorManager.register(new ReportCommandInteractor(this), null);
        interactorManager.register(new CallLimitCommandInteractor(this), null);
        interactorManager.register(new CoreCommandInteractor(this), null);
        interactorManager.register(new ConfirationCommandInteractor(this), null);
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
        System.out.println("\n" +
                " __   __ _                __  __  _               \n" +
                " \\ \\ / /(_)              |  \\/  |(_)              \n" +
                "  \\ V /  _   __ _   ___  | \\  / | _  _ __    __ _ \n" +
                "   > <  | | / _` | / _ \\ | |\\/| || || '_ \\  / _` |\n" +
                "  / . \\ | || (_| || (_) || |  | || || | | || (_| |\n" +
                " /_/ \\_\\|_| \\__,_| \\___/ |_|  |_||_||_| |_| \\__, |\n" +
                "                                             __/ |\n" +
                "                                            |___/ \n" +
                "                                        @" + AUTHOR + "\n" +
                "version: " + VERSION + "\n" +
                "github: " + GITHUB +
                "\n");
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
        lastStartTime = System.currentTimeMillis();
        getLog().info("小明机器人启动完成");
    }

    /**
     * 小明启动后的一些操作
     */
    void post() {
        responseGroupManager.sendMessageToTaggedGroup("log", "{xiaomingEnabled}");
    }

    /**
     * 文件存储信息载入和读取器
     */
    PreservableFactory<File> filePreservableFactory = new JsonFilePreservableFactory();

    /**
     * 统一权限管理器
     */
    @Setter
    File configDirectory = PathUtils.CONFIG;
    PermissionManager permissionManager;

    /**
     * 表情包管理器
     */
    LanguageManager languageManager;

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
    ConsoleInputThread consoleInputThread = new ConsoleInputThread(this);
    ConsoleXiaomingUser consoleXiaomingUser = new ConsoleXiaomingUserImpl(new ConsoleContactImpl(this, consoleInputThread));
    {
        consoleInputThread.setConsoleUser(consoleXiaomingUser);
    }

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

    /**
     * 定时任务
     */
    Scheduler scheduler;

    File logDirectory = PathUtils.LOG;

    XiaomingClassLoader xiaomingClassLoader = new XiaomingClassLoader(getClass().getClassLoader());

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