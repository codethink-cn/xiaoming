package com.chuanwise.xiaoming.api.bot;

import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.command.executor.CommandManager;
import com.chuanwise.xiaoming.api.config.Configuration;
import com.chuanwise.xiaoming.api.config.Statistician;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.event.UserInteractManager;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.url.PictureUrlManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.word.WordManager;
import com.chuanwise.xiaoming.api.event.EventListenerManager;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.runnable.RegularPreserveManager;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import net.mamoe.mirai.Bot;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * 小明机器人本体
 * @author Chuanwise
 */
public interface XiaomingBot {
    void execute(Runnable runnable);

    void execute(Thread thread);

    Bot getMiraiBot();

    void setMiraiBot(Bot bot);

    PreservableFactory<File> getFilePreservableFactory();

    ExecutorService getService();

    PermissionManager getPermissionManager();

    WordManager getWordManager();

    CommandManager getCommandManager();

    PluginManager getPluginManager();

    InteractorManager getInteractorManager();

    EventListenerManager getEventListenerManager();

    UserCallLimitManager getUserCallLimitManager();

    void start();

    default void stop() {
        stop(getConsoleXiaomingUser());
    }

    void load();

    boolean load(String name);

    void stop(XiaomingUser user);

    boolean isStop();

    Configuration getConfig();

    Statistician getStatistician();

    RegularPreserveManager getRegularPreserveManager();

    ConsoleXiaomingUser getConsoleXiaomingUser();

    void setConsoleXiaomingUser(ConsoleXiaomingUser consoleXiaomingUser);

    AccountManager getAccountManager();

    ResponseGroupManager getResponseGroupManager();

    UserInteractManager getUserInteractManager();

    ErrorMessageManager getErrorMessageManager();

    TextManager getTextManager();

    /**
     * 弃用的旧的 URL 请求器
     */
    // PictureUrlManager getPictureUrlManager();
}
