package cn.chuanwise.xiaoming.api.bot;

import cn.chuanwise.xiaoming.api.account.AccountManager;
import cn.chuanwise.xiaoming.api.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.api.configuration.Configuration;
import cn.chuanwise.xiaoming.api.configuration.Statistician;
import cn.chuanwise.xiaoming.api.contact.ContactManager;
import cn.chuanwise.xiaoming.api.error.ReportMessageManager;
import cn.chuanwise.xiaoming.api.event.EventManager;
import cn.chuanwise.xiaoming.api.interactor.InteractorManager;
import cn.chuanwise.xiaoming.api.license.LicenseManager;
import cn.chuanwise.xiaoming.api.limit.UserCallLimitManager;
import cn.chuanwise.xiaoming.api.permission.PermissionManager;
import cn.chuanwise.xiaoming.api.plugin.PluginManager;
import cn.chuanwise.xiaoming.api.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.api.resource.ResourceManager;
import cn.chuanwise.xiaoming.api.group.GroupRecordManager;
import cn.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import cn.chuanwise.toolkit.preservable.file.FileLoader;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.xiaoming.api.schedule.Scheduler;
import cn.chuanwise.xiaoming.api.language.Language;
import net.mamoe.mirai.Bot;
import org.slf4j.Logger;

import java.io.File;

/**
 * 小明机器人本体
 * @author Chuanwise
 */
public interface XiaomingBot {
    String VERSION = "2.5.1";
    String SPONSOR = "Chuanwise";
    String GITHUB = "https://github.com/Chuanwise/xiaoming-bot";
    String GROUP = "1028959718";
    String DEVELOPMENT_DOCUMENT = "https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Development.md";

    Bot getMiraiBot();

    void setMiraiBot(Bot bot);

    FileLoader getFileLoader();

    PermissionManager getPermissionManager();

    Language getLanguage();

    ContactManager getContactManager();

    InteractorManager getInteractorManager();

    PluginManager getPluginManager();

    EventManager getEventManager();

    UserCallLimitManager getUserCallLimitManager();

    void start();

    void load();

    boolean load(String name);

    void stop();

    boolean isStop();

    Configuration getConfiguration();

    Statistician getStatistician();

    ConsoleXiaomingUser getConsoleXiaomingUser();

    XiaomingClassLoader getXiaomingClassLoader();

    AccountManager getAccountManager();

    GroupRecordManager getGroupRecordManager();

    ReportMessageManager getReportMessageManager();

    ReceptionistManager getReceptionistManager();

    Logger getLog();

    LicenseManager getLicenseManager();

    Scheduler getScheduler();

    ResourceManager getResourceManager();

    void optimize();

    File getLogDirectory();

    Serializer getCoreSerializer();

    Serializer getSerializer();
}