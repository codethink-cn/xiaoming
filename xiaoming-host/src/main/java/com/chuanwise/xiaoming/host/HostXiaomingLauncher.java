package com.chuanwise.xiaoming.host;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.launcher.XiaomingLauncher;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.util.PathUtils;
import com.chuanwise.xiaoming.core.bot.XiaomingBotImpl;
import com.chuanwise.xiaoming.host.configuration.BotAccount;
import com.chuanwise.xiaoming.host.configuration.LauncherConfiguration;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservableFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;

import java.io.*;
import java.util.Objects;

/**
 * 小明机器人启动器
 */
@Slf4j
@Getter
public class HostXiaomingLauncher implements XiaomingLauncher {
    final XiaomingBot xiaomingBot = new XiaomingBotImpl();

    /**
     * 文件数据载入器
     */
    final PreservableFactory<File> filePreservableFactory = new JsonFilePreservableFactory();

    /**
     * 启动器设置
     */
    final LauncherConfiguration launcherConfiguration = filePreservableFactory.
            loadOrProduce(LauncherConfiguration.class, new File(PathUtils.LAUNCHER, "launcher.json"), LauncherConfiguration::new);

    /**
     * 读取机器人账号密码并准备登录
     */
    boolean readyLogin() {
        final BotAccount account = launcherConfiguration.getAccount();
        final File medium = launcherConfiguration.getMedium();
        if (!medium.isFile() || Objects.isNull(account)) {
            log.error("请在 " + medium.getAbsolutePath() + " 文件中写入机器人的账号密码");
            launcherConfiguration.save();
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
            }

            final Bot bot;
            final BotConfiguration configuration = new BotConfiguration();

            configuration.setProtocol(launcherConfiguration.getProtocol());
            configuration.setAutoReconnectOnForceOffline(launcherConfiguration.isAutoReconnectOnForceOffline());

            if (Objects.nonNull(md5)) {
                bot = BotFactory.INSTANCE.newBot(account.getQq(), md5, configuration);
            } else {
                bot = BotFactory.INSTANCE.newBot(account.getQq(), password);
            }

            xiaomingBot.setMiraiBot(bot);
            return true;
        } catch (Exception exception) {
            log.error("请检查位于 bots.json 中的账号信息是否正确");
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean launch() {
        try {
            return readyLogin();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return false;
        }
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public void stop() {
        xiaomingBot.stop();
    }
}