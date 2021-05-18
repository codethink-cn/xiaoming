package com.chuanwise.xiaoming.core.bot;

import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.classloader.XiaomingClassLoader;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.configuration.Statistician;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.exception.NoSuchBotException;
import com.chuanwise.xiaoming.api.exception.XiaomingInitializeException;
import com.chuanwise.xiaoming.api.exception.XiaomingRuntimeException;
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.object.XiaomingThread;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.url.PictureUrlManager;
import com.chuanwise.xiaoming.api.user.Receptionist;
import com.chuanwise.xiaoming.api.user.ReceptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.api.word.WordManager;
import com.chuanwise.xiaoming.api.event.EventListenerManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.thread.RegularPreserveManager;
import com.chuanwise.xiaoming.core.account.AccountManagerImpl;
import com.chuanwise.xiaoming.core.error.ErrorMessageManagerImpl;
import com.chuanwise.xiaoming.core.interactor.core.ReportInteractor;
import com.chuanwise.xiaoming.core.interactor.InteractorManagerImpl;
import com.chuanwise.xiaoming.core.interactor.core.*;
import com.chuanwise.xiaoming.core.license.LicenceManagerImpl;
import com.chuanwise.xiaoming.core.response.ResponseGroupManagerImpl;
import com.chuanwise.xiaoming.core.text.TextManagerImpl;
import com.chuanwise.xiaoming.core.thread.ConsoleInputThread;
import com.chuanwise.xiaoming.core.thread.RegularPreserveManagerImpl;
import com.chuanwise.xiaoming.core.config.ConfigurationImpl;
import com.chuanwise.xiaoming.core.config.StatisticianImpl;
import com.chuanwise.xiaoming.core.url.PictureUrlManagerImpl;
import com.chuanwise.xiaoming.core.recept.ReceptionistManagerImpl;
import com.chuanwise.xiaoming.core.user.ConsoleXiaomingUserImpl;
import com.chuanwise.xiaoming.core.user.XiaomingUserImpl;
import com.chuanwise.xiaoming.core.word.WordManagerImpl;
import com.chuanwise.xiaoming.core.event.EventListenerManagerImpl;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.core.limit.UserCallLimitManagerImpl;
import com.chuanwise.xiaoming.core.permission.PermissionManagerImpl;
import com.chuanwise.xiaoming.core.plugin.PluginManagerImpl;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservableFactory;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.util.PathUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 小明机器人核心
 * @author Chuanwise
 */
@NoArgsConstructor
@Getter
@Slf4j
public class XiaomingBotImpl implements XiaomingBot {
    public static final String VERSION = "1.0 TEST";
    public static final String AUTHOR = "Chuanwise";
    public static final String GITHUB = "https://github.com/Chuanwise/xiaoming-bot";

    @Override
    public Logger getLog() {
        return log;
    }

    /**
     * mirai 机器人引用
     */
    Bot miraiBot;

    public XiaomingBotImpl(Bot miraiBot) {
        this.miraiBot = miraiBot;
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

    @Override
    public void load() {
        load("statistician");
        load("responseGroupManager");
        load("accountManager");
        load("regularPreserveManager");
        load("statistician");
        load("permissionManager");
        load("wordManager");
        load("pluginManager");
        load("interactorManager");
        load("eventListenerManager");
        load("userCallLimitManager");
        load("userInteractManager");
        load("config");
        load("errorMessageManager");
        load("urlInMiraiCodeManager");
        load("textManager");
        load("receptionistManager");
        load("licenseManager");
        load("consoleInputThread");
    }

    /**
     * 创建一些小明必要的的文件夹
     */
    void makeDirectories() {
        if (!accountDirectory.isDirectory() && !accountDirectory.mkdirs()) {
            final String message = "无法创建账户文件夹：" + accountDirectory.getAbsolutePath();
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

        if (!textDirectory.isDirectory() && !textDirectory.mkdirs()) {
            final String message = "无法创建文本文件夹：" + textDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        if (!logDirectory.isDirectory() && !logDirectory.mkdirs()) {
            final String message = "无法创建日志文件夹：" + logDirectory.getAbsolutePath();
            throw new XiaomingInitializeException(message);
        }

        File lastestLog = new File(logDirectory, "lastest.log");
        if (lastestLog.isFile()) {
            final File dest = new File(logDirectory, TimeUtil.FORMAT.format(lastestLog.lastModified()) + ".log");
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
        interactorManager.register(new TextCommandInteractor(this), null);

        interactorManager.register(new PluginInteractor(this), null);
        interactorManager.register(new AccountCommandInteractor(this), null);
        interactorManager.register(new ErrorCommandInteractor(this), null);
        interactorManager.register(new CallLimitCommandInteractor(this), null);
        interactorManager.register(new CoreCommandInteractor(this), null);
        interactorManager.register(new PermissionCommandInteractor(this), null);
        interactorManager.register(new ResponseGroupCommandInteractor(this), null);
        interactorManager.register(new WordCommandInteractor(this), null);
        // 注册内核交互器
        interactorManager.register(new ReportInteractor(), null);
        interactorManager.denyCoreRegister();

        // 注册内核监听器
        eventListenerManager.register(receptionistManager, null);
        eventListenerManager.denyCoreRegister();

        // 设置调用限制
        userCallLimitManager.getGroupCallLimiter().setConfig(configuration.getGroupCallConfig());
        userCallLimitManager.getPrivateCallLimiter().setConfig(configuration.getPrivateCallConfig());
    }

    void initialize() {
        makeDirectories();

        load();

        registerCoreModules();

        // 加载所有的插件
        try {
            pluginManager.loadAllPlugins(consoleXiaomingUser);
        } catch (Throwable throwable) {
            getLog().error("加载所有插件时出现异常：", throwable);
        }

        // 将 mirai 的事件转发到小明的中央消息处理器
        final EventChannel<BotEvent> eventChannel = miraiBot.getEventChannel();
        eventChannel.registerListenerHost(new ListenerHost() {
            @EventHandler
            public void onEvent(Event event) {
                eventListenerManager.callLater(event);
            }
        });
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

        execute(eventListenerManager);

        post();
        getLog().info("小明机器人启动完成");
    }

    /**
     * 小明启动后的一些操作
     */
    void post() {
        responseGroupManager.sendMessageToTaggedGroup("tag", "小明启动成功 " + wordManager.get("happy"));
    }

    /**
     * 文件存储信息载入和读取器
     */
    PreservableFactory<File> filePreservableFactory = new JsonFilePreservableFactory();

    /**
     * 线程池
     */
    ExecutorService service = Executors.newFixedThreadPool(100);

    @Override
    public void execute(Runnable runnable) {
        service.execute(runnable);
    }

    @Override
    public void execute(Thread thread) {
        service.execute(thread);
    }

    /**
     * 统一权限管理器
     */
    @Setter
    File configDirectory = PathUtil.CONFIG_DIR;
    PermissionManager permissionManager;

    /**
     * 表情包管理器
     */
    WordManager wordManager;

    /**
     * 插件管理器
     */
    @Setter
    File pluginDirectory = PathUtil.PLUGIN_DIR;
    PluginManager pluginManager;

    /**
     * 交互器管理器
     */
    InteractorManager interactorManager;

    /**
     * 监听器管理器
     */
    EventListenerManager eventListenerManager;

    /**
     * 用户调用限制管理器
     */
    UserCallLimitManager userCallLimitManager;

    /**
     * 小明基本设置
     */
    Configuration configuration;

    /**
     * 加载小明的某个组件
     * @param name 组件名
     * @return 是否找到该组件
     */
    @Override
    public boolean load(String name) {
        switch (name) {
            case "config":
                configuration = filePreservableFactory
                        .loadOrProduce(ConfigurationImpl.class, new File(configDirectory, "configurations.json"), ConfigurationImpl::new);
                configuration.setXiaomingBot(this);
                return true;
            case "userCallLimitManager":
                userCallLimitManager = filePreservableFactory
                    .loadOrProduce(UserCallLimitManagerImpl.class, new File(configDirectory, "limits.json"), UserCallLimitManagerImpl::new);
                userCallLimitManager.setXiaomingBot(this);
                return true;
            case "eventListenerManager":
                eventListenerManager = new EventListenerManagerImpl(this);
                return true;
            case "interactorManager":
                interactorManager = new InteractorManagerImpl(this);
                return true;
            case "pluginManager":
                pluginManager = new PluginManagerImpl(this, pluginDirectory);
                return true;
            case "wordManager":
                wordManager = filePreservableFactory
                        .loadOrProduce(WordManagerImpl.class, new File(configDirectory, "words.json"), WordManagerImpl::new);
                wordManager.setXiaomingBot(this);
                return true;
            case "permissionManager":
                permissionManager = filePreservableFactory
                        .loadOrProduce(PermissionManagerImpl.class, new File(configDirectory, "permissions.json"), () -> {
                            PermissionManagerImpl manager = new PermissionManagerImpl();
                            manager.setGroups(new HashMap<>());
                            return manager;
                        });
                permissionManager.setXiaomingBot(this);
                return true;
            case "statistician":
                statistician = filePreservableFactory
                        .loadOrProduce(StatisticianImpl.class, new File(configDirectory, "counters.json"), StatisticianImpl::new);
                statistician.setXiaomingBot(this);
                return true;
            case "regularPreserveManager":
                regularPreserveManager = new RegularPreserveManagerImpl(this);
                return true;
            case "accountManager":
                accountManager = new AccountManagerImpl(this, accountDirectory);
                return true;
            case "responseGroupManager":
                responseGroupManager = filePreservableFactory
                        .loadOrProduce(ResponseGroupManagerImpl.class, new File(configDirectory, "groups.json"), ResponseGroupManagerImpl::new);
                responseGroupManager.setXiaomingBot(this);
                return true;
            case "receptionistManager":
                receptionistManager = new ReceptionistManagerImpl(this);
                return true;
            case "errorMessageManager":
                errorMessageManager = filePreservableFactory
                        .loadOrProduce(ErrorMessageManagerImpl.class, new File(configDirectory, "errors.json"), ErrorMessageManagerImpl::new);
                errorMessageManager.setXiaomingBot(this);
                return true;
            case "urlInMiraiCodeManager":
                pictureUrlManager = filePreservableFactory
                        .loadOrProduce(PictureUrlManagerImpl.class, new File(configDirectory, "urls.json"), PictureUrlManagerImpl::new);
                pictureUrlManager.setXiaomingBot(this);
                return true;
            case "textManager":
                textManager = new TextManagerImpl(this, textDirectory);
                return true;
            case "licenseManager":
                licenseManager = filePreservableFactory
                        .loadOrProduce(LicenceManagerImpl.class, new File(configDirectory, "license.json"), LicenceManagerImpl::new);
                licenseManager.setXiaomingBot(this);
                return true;
            case "consoleInputThread":
                if (Objects.nonNull(consoleInputThread)) {
                    consoleInputThread.stop();
                }
                consoleInputThread = new ConsoleInputThread(this);
                service.execute(consoleInputThread);
                return true;
            default:
                return false;
        }
    }

    /**
     * 小明统计数据
     */
    Statistician statistician;

    /**
     * 定时数据保存器
     */
    RegularPreserveManager regularPreserveManager;

    /**
     * 机器人正在执行的标记，默认是 {@code true}，需要使用 start 启动
     */
    volatile boolean stop = true;

    /**
     * 控制台小明使用者
     */
    XiaomingUser consoleXiaomingUser = new ConsoleXiaomingUserImpl(this);
    XiaomingThread consoleInputThread;

    /**
     * 用户数据管理器
     */
    @Setter
    File accountDirectory = PathUtil.ACCOUNT_DIR;
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
    ErrorMessageManager errorMessageManager;

    /**
     * url 资源定期请求器
     */
    @Deprecated
    PictureUrlManager pictureUrlManager;

    /**
     * 用户须知管理器
     */
    LicenseManager licenseManager;

    /**
     * 提示文字管理器
     */
    File textDirectory = PathUtil.TEXT_DIR;
    TextManager textManager;

    File logDirectory = PathUtil.LOG_DIR;

    XiaomingClassLoader xiaomingClassLoader = new XiaomingClassLoader(getClass().getClassLoader());

    void shutdownService(XiaomingUser user) {
        // 唤醒正在等待事件的进程，令其退出
        final Queue<Event> events = eventListenerManager.getEvents();
        synchronized (events) {
            events.notifyAll();
        }

        for (Map.Entry<Long, Receptionist> entry : receptionistManager.getReceptionists().entrySet()) {
            final Receptionist receptionist = entry.getValue();
            final XiaomingUser currentUser = receptionist.getUser();

            try {
                receptionist.stop();
            } catch (XiaomingRuntimeException exception) {
                receptionist.forceStop();
                if (currentUser != user) {
                    getLog().warn("正等待用户" + currentUser.getName() + "的下一次输入，已终止此次交互");
                    currentUser.sendError(user.getName() + "正在关闭小明，我们下次见哦");
                }
            }
        }

        // 给线程池下关闭命令，等待 10 秒后检查是否成功关闭
        service.shutdown();

        // 如果还没关闭就尝试关闭一下
        if (!service.isShutdown()) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ignored) {
            }
            try {
                int remainTryTimes = 5;
                while (!service.awaitTermination(5, TimeUnit.SECONDS) && remainTryTimes > 0) {
                    getLog().warn("线程仍然没有全部结束，请稍等，小明还会尝试 " + remainTryTimes + " 次……");
                    remainTryTimes--;
                }
            } catch (InterruptedException exception) {
                getLog().warn("等待线程池关闭被强行中止");
            }
        }
    }

    @Override
    public synchronized void stop(XiaomingUser user) {
        if (isStop()) {
            throw new XiaomingRuntimeException("can not stop a stopped xiaoming bot");
        }

        user.sendMessage("正在尝试关闭小明");
        stop = true;

        // 保存所有的文件
        regularPreserveManager.save();

        getConsoleInputThread().stop();

        shutdownService(user);

        if (service.isShutdown()) {
            getLog().info("线程池成功关闭");
        } else {
            getLog().error("这些线程无法立即关闭：");
            for (Runnable runnable : service.shutdownNow()) {
                getLog().error(runnable.getClass().getName());
            }
            getLog().error("已放弃关闭这些线程");
            user.sendError("线程池最终没有全部结束");
        }

        // 关闭所有的插件
        for (XiaomingPlugin plugin : pluginManager.getEnabledPlugins()) {
            getLog().info("正在关闭插件：{}", plugin.getCompleteName());
            try {
                plugin.onDisable();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        for (XiaomingPlugin plugin : pluginManager.getLoadedPlugins()) {
            getLog().info("正在卸载插件：{}", plugin.getCompleteName());
            try {
                plugin.onUnload();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        getLog().info("正在关闭 mirai 机器人");
        user.sendMessage("已尝试关闭小明");

        miraiBot.close();
    }
}