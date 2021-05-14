package com.chuanwise.xiaoming.api.bot;

import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.classloader.XiaomingClassLoader;
import com.chuanwise.xiaoming.api.config.Configuration;
import com.chuanwise.xiaoming.api.config.Statistician;
import com.chuanwise.xiaoming.api.error.ErrorMessageManager;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.user.ReceiptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.word.WordManager;
import com.chuanwise.xiaoming.api.event.EventListenerManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.runnable.RegularPreserveManager;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import org.slf4j.Logger;

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

    InteractorManager getInteractorManager();

    PluginManager getPluginManager();

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

    XiaomingUser getConsoleXiaomingUser();

    AccountManager getAccountManager();

    ResponseGroupManager getResponseGroupManager();

    ErrorMessageManager getErrorMessageManager();

    TextManager getTextManager();

    void setConsoleXiaomingUser(XiaomingUser xiaomingUser);

    // XiaomingClassLoader getXiaomingClassLoader();

    ReceiptionistManager getReceiptionistManager();

    Logger getLog();
}
