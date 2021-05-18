package com.chuanwise.xiaoming.api.bot;

import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.configuration.Statistician;
import com.chuanwise.xiaoming.api.error.ReportMessageManager;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.object.XiaomingThread;
import com.chuanwise.xiaoming.api.text.TextManager;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.word.WordManager;
import com.chuanwise.xiaoming.api.event.EventListenerManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.thread.RegularPreserveManager;
import net.mamoe.mirai.Bot;
import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * 小明机器人本体
 * @author Chuanwise
 */
public interface XiaomingBot {
    long getLastStartTime();

    void execute(Runnable runnable);

    void execute(Thread thread);

    Bot getMiraiBot();

    void setMiraiBot(Bot bot);

    PreservableFactory<File> getFilePreservableFactory();

    ExecutorService getMainThreadPool();

    PermissionManager getPermissionManager();

    WordManager getWordManager();

    InteractorManager getInteractorManager();

    PluginManager getPluginManager();

    EventListenerManager getEventListenerManager();

    UserCallLimitManager getUserCallLimitManager();

    void start();

    XiaomingThread getConsoleInputThread();

    default void stop() {
        stop(getConsoleXiaomingUser());
    }

    void load();

    boolean load(String name);

    void stop(XiaomingUser user);

    boolean isStop();

    Configuration getConfiguration();

    Statistician getStatistician();

    RegularPreserveManager getRegularPreserveManager();

    XiaomingUser getConsoleXiaomingUser();

    AccountManager getAccountManager();

    ResponseGroupManager getResponseGroupManager();

    ReportMessageManager getReportMessageManager();

    TextManager getTextManager();

    // XiaomingClassLoader getXiaomingClassLoader();

    ReceptionistManager getReceptionistManager();

    Logger getLog();

    LicenseManager getLicenseManager();
}
