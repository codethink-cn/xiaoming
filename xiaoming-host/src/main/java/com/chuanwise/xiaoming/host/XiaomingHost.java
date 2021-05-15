package com.chuanwise.xiaoming.host;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.MD5Utils;
import com.chuanwise.xiaoming.api.util.PathUtil;
import com.chuanwise.xiaoming.core.bot.XiaomingBotImpl;
import com.chuanwise.xiaoming.host.config.BotAccount;
import com.chuanwise.xiaoming.host.config.LauncherConfig;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservableFactory;
import com.chuanwise.xiaoming.host.runnable.ConsoleListenerRunnable;
import com.chuanwise.xiaoming.host.user.ConsoleXiaomingUser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.BotFactory;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 小明机器人启动器
 */
@Slf4j
@Getter
public class XiaomingHost {
    final XiaomingBot xiaomingBot = new XiaomingBotImpl();

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
        final File medium = launcherConfig.getMedium();
        if (!medium.isFile() || Objects.isNull(account)) {
            log.error("请在 " + medium.getAbsolutePath() + " 文件中写入机器人的账号密码");
            launcherConfig.save();
            return false;
        }

        if (Objects.isNull(account.getMd5())) {
            account.setPassword(account.getPassword());
        }

        try {
            final String password = account.getPassword();
            final byte[] md5 = account.getMd5();
            if (Objects.isNull(password) && Objects.isNull(md5)) {
                log.error("请检查位于 bots.json 中的账号信息是否正确。password 和 md5 属性至少要有一个");
                return false;
            } else if (Objects.nonNull(md5)) {
                xiaomingBot.setMiraiBot(BotFactory.INSTANCE.newBot(account.getQq(), md5));
            } else {
                xiaomingBot.setMiraiBot(BotFactory.INSTANCE.newBot(account.getQq(), password));
            }
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
}