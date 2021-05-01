package com.taixue.xiaoming.bot.host;

import com.taixue.xiaoming.bot.api.config.BotAccount;
import com.taixue.xiaoming.bot.api.command.executor.CommandManager;
import com.taixue.xiaoming.bot.api.config.BotAccountConfig;
import com.taixue.xiaoming.bot.api.data.RegularSaveDataManager;
import com.taixue.xiaoming.bot.api.group.Group;
import com.taixue.xiaoming.bot.api.bot.XiaomingBot;
import com.taixue.xiaoming.bot.api.listener.dispatcher.user.ConsoleDispatcherUser;
import com.taixue.xiaoming.bot.api.listener.interactor.InteractorManager;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.core.bot.XiaomingBotImpl;
import com.taixue.xiaoming.bot.core.listener.dispatcher.user.ConsoleDispatcherUserImpl;
import com.taixue.xiaoming.bot.core.runnable.RegularCounterSaveRunnable;
import com.taixue.xiaoming.bot.host.Interactor.CoreInteractor;
import com.taixue.xiaoming.bot.host.command.executor.*;
import com.taixue.xiaoming.bot.host.runnable.ConsoleCommandRunnable;
import com.taixue.xiaoming.bot.util.PathUtil;
import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotRegisterInfo;
import love.forte.simbot.bot.NoSuchBotException;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import net.mamoe.mirai.contact.BotIsBeingMutedException;
import org.slf4j.Logger;
import sun.misc.Signal;

import com.taixue.xiaoming.bot.host.hook.ShutdownHook;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 小明的启动器类
 */
@SimbotApplication
public class XiaomingLauncher extends HostObjectImpl implements SimbotProcess {
    static final Random RANDOM = new Random();
    public static final String VERSION = "1.0 TEST";
    public static final String AUTHOR = "Chuanwise";
    public static final String GITHUB = "https://github.com/Chuanwise/xiaoming-bot";

    /**
     * 小明本体
     */
    static final XiaomingBot XIAOMING_BOT = new XiaomingBotImpl();

    /**
     * 小明启动器
     */
    static final XiaomingLauncher INSTANCE = new XiaomingLauncher();

    public static XiaomingLauncher getInstance() {
        return INSTANCE;
    }

    /**
     * 小明控制台指令发送者
     */
    final ConsoleDispatcherUser consoleXiaomingUser = new ConsoleDispatcherUserImpl();

    public ConsoleDispatcherUser getConsoleXiaomingUser() {
        return consoleXiaomingUser;
    }

    /**
     * 一些内核注册的指令处理器
     */
    final CoreCommandExecutor coreCommandExecutor = new CoreCommandExecutor();
    final GroupCommandExecutor groupCommandExecutor = new GroupCommandExecutor();
    final EmojiCommandExecutor emojiCommandExecutor = new EmojiCommandExecutor();
    final PermissionCommandExecutor permissionCommandExecutor = new PermissionCommandExecutor();
    final AccountCommandExecutor accountCommandExecutor = new AccountCommandExecutor();
    final CallLimitCommandExecutor callLimitCommandExecutor = new CallLimitCommandExecutor();
    final DebugCommandExecutor debugCommandExecutor = new DebugCommandExecutor();

    final ConsoleCommandRunnable consoleCommandRunnable = new ConsoleCommandRunnable();

    public ConsoleCommandRunnable getConsoleCommandRunnable() {
        return consoleCommandRunnable;
    }

    /**
     * 定时保存相关运行时数据
     */
    final RegularCounterSaveRunnable regularCounterSaveRunnable = new RegularCounterSaveRunnable();

    final CoreInteractor coreInteractor = new CoreInteractor();

    final ShutdownHook shutdownHook = new ShutdownHook();

    public ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    /**
     * 登录所有的机器人账号
     * @param context 上下文
     */
    public boolean loginBots(final SimbotContext context) {
        // 登录 Bot 账号
        final BotAccountConfig config = getXiaomingBot().getBotAccountConfig();
        final List<BotAccount> accounts = config.getAccounts();

        if (!config.getFile().exists() || accounts.isEmpty()) {
            config.readySave();
            getLogger().error("请打开 {} 并设置机器人账号密码", config.getFile().getAbsolutePath());
            return false;
        } else {
            for (BotAccount account : accounts) {
                try {
                    context.getBotManager().registerBot(new BotRegisterInfo(String.valueOf(account.getQq()), account.getPassword()));
                } catch (Exception exception) {
                    getLogger().error("登录账号：{} 时出现异常：{}", account.getQq(), exception);
                    exception.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 初始化小明需要的文件系统
     */
    public void initFiles(final SimbotContext context) {
        // 复制默认的表情文件
        /*
        final File emojisFile = getXiaomingBot().getEmojiManager().getFile();
        if (!emojisFile.exists()) {
            try {
                FileUtil.copyResource("default/emojis", emojisFile);
            } catch (IOException ignored) {
            }
        }

         */
    }

    public void enableCore() {
        final XiaomingBot xiaomingBot = getXiaomingBot();

        // 注册内核指令处理器
        final CommandManager commandManager = xiaomingBot.getCommandManager();
        commandManager.registerAsCore(coreCommandExecutor);
        commandManager.registerAsCore(groupCommandExecutor);
        commandManager.registerAsCore(emojiCommandExecutor);
        commandManager.registerAsCore(permissionCommandExecutor);
        commandManager.registerAsCore(accountCommandExecutor);
        commandManager.registerAsCore(callLimitCommandExecutor);
        commandManager.registerAsCore(debugCommandExecutor);

        // 加载内核交互器
        final InteractorManager interactorManager = xiaomingBot.getInteractorManager();
        // 测试用交互器
        // interactorManager.registerAsCore(coreInteractor);

        // 启动控制台指令线程
        xiaomingBot.execute(consoleCommandRunnable);
        // 启动定期保存运行数据线程
        xiaomingBot.execute(regularCounterSaveRunnable);

        // 设置调用限制
        xiaomingBot.getUserCallLimitManager().getGroupCallLimiter().setConfig(xiaomingBot.getConfig().getGroupCallConfig());
        xiaomingBot.getUserCallLimitManager().getPrivateCallLimiter().setConfig(xiaomingBot.getConfig().getPrivateCallConfig());

        // 加载插件
        final PluginManager pluginManager = xiaomingBot.getPluginManager();
        pluginManager.loadAllPlugins(consoleXiaomingUser);
    }

    /**
     * 初始化并启动小明框架
     * @param context 上下文
     */
    @Override
    public void post(final SimbotContext context) {
        final Logger logger = getLogger();
        // 登录机器人账号
        if (!loginBots(context)) {
            return;
        }

        initFiles(context);

        final XiaomingBot xiaomingBot = getXiaomingBot();
        final Bot defaultBot;
        final BotSender sender;

        // 获取已经登录的 Bot 账号并设置送信器
        try {
            defaultBot = context.getBotManager().getDefaultBot();
            sender = defaultBot.getSender();
            ((XiaomingBotImpl) xiaomingBot).setMsgSender(sender);
        } catch (NoSuchBotException exception) {
            logger.error("没有任何一个可以登录的机器人 QQ 账号，请打开 {} 并核对机器人账号密码是否正确", getXiaomingBot().getBotAccountConfig().getFile().getAbsolutePath());
            return;
        }

        // 启动内核
        enableCore();

        // 在所有的日志群通知小明已启动
        for (Group logGroup : xiaomingBot.getGroupManager().forTag("log")) {
            try {
                sender.SENDER.sendGroupMsg(logGroup.getCode(), "小明正常启动 " + getXiaomingBot().getEmojiManager().get("happy"));
            } catch (NoSuchElementException exception) {
                logger.error("小明找到日志群 {}", logGroup.getCode());
            } catch (BotIsBeingMutedException exception) {
                logger.error("小明在日志群 {} 被禁言了，无法发送消息", logGroup.getCode());
            } catch (Exception exception) {
                logger.error("小明无法在日志群 {} 发消息，因为出现异常：{}", logGroup.getCode(), exception);
                exception.printStackTrace();
            }
        }
        logger.info("小明正常启动 " + getXiaomingBot().getEmojiManager().get("happy"));
    }

    @Override
    public void pre(final Configuration config) {
        System.out.println();
        System.out.println(" __   __ _                __  __  _               \n" +
                " \\ \\ / /(_)              |  \\/  |(_)              \n" +
                "  \\ V /  _   __ _   ___  | \\  / | _  _ __    __ _ \n" +
                "   > <  | | / _` | / _ \\ | |\\/| || || '_ \\  / _` |\n" +
                "  / . \\ | || (_| || (_) || |  | || || | | || (_| |\n" +
                " /_/ \\_\\|_| \\__,_| \\___/ |_|  |_||_||_| |_| \\__, |\n" +
                "                                             __/ |\n" +
                "                                            |___/ \n" +
                "                                        @" + AUTHOR + "\n" +
                "version: " + VERSION + "\n" +
                "github: " + GITHUB + "\n");

        // 设置关闭时的数据保存操作
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
    }

    public static void main(final String[] args) {
        // 创建相关重要文件夹
        PathUtil.CONFIG_DIR.mkdirs();
        PathUtil.PLUGIN_DIR.mkdirs();
        PathUtil.ACCOUNT_DIR.mkdirs();

        // 启动小明
        SimbotApp.run(XiaomingLauncher.class, args);
    }
}