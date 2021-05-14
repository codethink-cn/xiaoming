package com.chuanwise.xiaoming.host;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.PathUtil;
import com.chuanwise.xiaoming.core.bot.XiaomingBotImpl;
import com.chuanwise.xiaoming.core.plugin.PluginPropertyImpl;
import com.chuanwise.xiaoming.core.plugin.XiaomingPluginImpl;
import com.chuanwise.xiaoming.host.config.BotAccount;
import com.chuanwise.xiaoming.host.config.LauncherConfig;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservableFactory;
import com.chuanwise.xiaoming.host.interactor.GroupInteractorTest;
import com.chuanwise.xiaoming.host.interactor.InteractorTest;
import com.chuanwise.xiaoming.host.runnable.ConsoleListenerRunnable;
import com.chuanwise.xiaoming.host.user.ConsoleXiaomingUser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.BotFactory;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 小明机器人启动器
 */
@Slf4j
@Getter
public class XiaomingLauncher {
    public XiaomingBot xiaomingBot = new XiaomingBotImpl();

    /**
     * 文件数据载入器
     */
    final PreservableFactory<File> filePreservableFactory = new JsonFilePreservableFactory();

    /**
     * 启动器设置
     */
    final LauncherConfig launcherConfig = filePreservableFactory.
            loadOrProduce(LauncherConfig.class, new File(PathUtil.LAUNCHER_DIR, "launcher.json"), LauncherConfig::new);

    final XiaomingUser consoleXiaomingUser = new ConsoleXiaomingUser(xiaomingBot);

    /**
     * 控制台指令接收线程
     */
    final ConsoleListenerRunnable consoleListenerRunnable = new ConsoleListenerRunnable(xiaomingBot);

    /**
     * 读取机器人账号密码并准备登录
     */
    boolean readyLogin() {
        final BotAccount account = launcherConfig.getAccount();
        if (!launcherConfig.getMedium().isFile() || Objects.isNull(account)) {
            log.error("请在 bots.json 文件夹中写入机器人的账号密码");
            launcherConfig.save();
            return false;
        }

        if (Objects.isNull(account.getMd5())) {
            account.setPassword(account.getPassword());
        }

        try {
            xiaomingBot.setMiraiBot(BotFactory.INSTANCE.newBot(account.getQq(), account.getMd5()));
            return true;
        } catch (Exception exception) {
            log.error("请检查位于 bots.json 中的账号信息是否正确");
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * 载入一大堆设置
     * @return
     */
    public boolean launch() {
        try {
            // 尝试设置账号密码
            if (!readyLogin()) {
                return false;
            }

            // 设置控制台小明使用者
            xiaomingBot.setConsoleXiaomingUser(consoleXiaomingUser);
            return start();
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("小明启动失败");
            return false;
        }
    }

    /**
     * 启动小明
     */
    public boolean start() {
        try {
            xiaomingBot.start();
        } catch (Exception exception) {
            log.error("启动小明时出现异常：" + exception);
            exception.printStackTrace();
            return false;
        }

        // 启动控制台指令接收
        final ExecutorService service = xiaomingBot.getService();
        service.execute(consoleListenerRunnable);
        return true;
    }

    /**
     * 关闭小明
     */
    public void stop() {
        xiaomingBot.stop();
    }

    public static void main(String[] args) {
        final File launcherDir = PathUtil.LAUNCHER_DIR;
        if (!launcherDir.isDirectory() && !launcherDir.mkdirs()) {
            log.error("无法创建启动器配置文件夹：" + launcherDir.getAbsolutePath());
            return;
        }

        final XiaomingLauncher launcher = new XiaomingLauncher();

        // 设置关闭时的数据保存操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 如果小明此时还没有关闭则关闭
            if (!launcher.getXiaomingBot().isStop()) {
                launcher.stop();
            }
        }));

        launcher.launch();

        // launch 后，从 launcher 获得已经被启动的小明实例
        final XiaomingBot xiaomingBot = launcher.getXiaomingBot();

        // 小明已经启动完成，不能再以内核身份注册指令，所以只能伪装成一个插件
        // 创建插件实例
        final XiaomingPluginImpl plugin = new XiaomingPluginImpl();
        // 设置插件对应的插件文件
        plugin.setProperty(new PluginPropertyImpl(new File("测试插件.jar")));
        // 设置插件日志器
        plugin.setLog(LoggerFactory.getLogger(plugin.getCompleteName()));

        xiaomingBot.getInteractorManager().register(new GroupInteractorTest(), plugin);
        xiaomingBot.getInteractorManager().register(new InteractorTest(), plugin);
    }
}