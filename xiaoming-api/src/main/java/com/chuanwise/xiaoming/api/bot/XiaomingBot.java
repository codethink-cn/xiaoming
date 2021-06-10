package com.chuanwise.xiaoming.api.bot;

import com.chuanwise.xiaoming.api.account.AccountManager;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.configuration.Statistician;
import com.chuanwise.xiaoming.api.contact.ContactManager;
import com.chuanwise.xiaoming.api.error.ReportMessageManager;
import com.chuanwise.xiaoming.api.event.EventManager;
import com.chuanwise.xiaoming.api.interactor.InteractorManager;
import com.chuanwise.xiaoming.api.license.LicenseManager;
import com.chuanwise.xiaoming.api.resource.ResourceManager;
import com.chuanwise.xiaoming.api.recept.ReceptionistManager;
import com.chuanwise.xiaoming.api.schedule.Scheduler;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.language.LanguageManager;
import com.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import com.chuanwise.xiaoming.api.permission.PermissionManager;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.preserve.PreservableFactory;
import com.chuanwise.xiaoming.api.response.ResponseGroupManager;
import com.chuanwise.xiaoming.api.thread.Finalizer;
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

    Bot getMiraiBot();

    void setMiraiBot(Bot bot);

    PreservableFactory<File> getFilePreservableFactory();

    PermissionManager getPermissionManager();

    LanguageManager getLanguageManager();

    ContactManager getContactManager();

    InteractorManager getInteractorManager();

    PluginManager getPluginManager();

    EventManager getEventManager();

    UserCallLimitManager getUserCallLimitManager();

    void start();

    Runnable getConsoleInputThread();

    default void stop() {
        stop(getConsoleXiaomingUser());
    }

    void load();

    boolean load(String name);

    void stop(XiaomingUser user);

    boolean isStop();

    Configuration getConfiguration();

    Statistician getStatistician();

    Finalizer getFinalizer();

    XiaomingUser getConsoleXiaomingUser();

    AccountManager getAccountManager();

    ResponseGroupManager getResponseGroupManager();

    ReportMessageManager getReportMessageManager();

    ReceptionistManager getReceptionistManager();

    Logger getLog();

    LicenseManager getLicenseManager();

    Scheduler getScheduler();

    ResourceManager getResourceManager();

    void optimize();
}
