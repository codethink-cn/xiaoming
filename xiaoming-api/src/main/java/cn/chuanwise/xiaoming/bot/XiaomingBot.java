package cn.chuanwise.xiaoming.bot;

import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.ContactManager;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.report.ReportMessageManager;
import cn.chuanwise.xiaoming.event.EventManager;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.license.LicenseManager;
import cn.chuanwise.xiaoming.limit.UserCallLimitManager;
import cn.chuanwise.xiaoming.permission.PermissionManager;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.resource.ResourceManager;
import cn.chuanwise.xiaoming.group.GroupRecordManager;
import cn.chuanwise.xiaoming.optimize.Optimizer;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.toolkit.preservable.file.FileLoader;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.xiaoming.schedule.Scheduler;
import cn.chuanwise.xiaoming.language.Language;
import net.mamoe.mirai.Bot;
import org.slf4j.Logger;

import java.beans.Transient;
import java.io.File;

/**
 * 小明机器人本体
 * @author Chuanwise
 */
public interface XiaomingBot {
    String VERSION = "3.1.5";
    String SPONSOR = "Chuanwise";
    String GITHUB = "https://github.com/Chuanwise/xiaoming-bot";
    String GROUP = "1028959718";
    String DEVELOPMENT_DOCUMENT = "https://github.com/Chuanwise/xiaoming-bot/tree/main/docs/Development.md";

    Bot getMiraiBot();

    void setMiraiBot(Bot bot);

    FileLoader getFileLoader();

    void setFileLoader(FileLoader fileLoader);

    PermissionManager getPermissionManager();

    void setPermissionManager(PermissionManager permissionManager);

    LanguageManager getLanguageManager();

    void setLanguageManager(LanguageManager languageManager);

    ContactManager getContactManager();

    void setContactManager(ContactManager contactManager);

    InteractorManager getInteractorManager();

    void setInteractorManager(InteractorManager interactorManager);

    PluginManager getPluginManager();

    void setPluginManager(PluginManager pluginManager);

    EventManager getEventManager();

    void setEventManager(EventManager eventManager);

    UserCallLimitManager getUserCallLimitManager();

    void setUserCallLimitManager(UserCallLimitManager userCallLimitManager);

    void start();

    void load();

    boolean load(String name);

    void stop();

    boolean isStop();

    Configuration getConfiguration();

    void setConfiguration(Configuration configuration);

    Statistician getStatistician();

    void setStatistician(Statistician statistician);

    ConsoleXiaomingUser getConsoleXiaomingUser();

    void setConsoleXiaomingUser(ConsoleXiaomingUser consoleXiaomingUser);

    XiaomingClassLoader getXiaomingClassLoader();

    void setXiaomingClassLoader(XiaomingClassLoader xiaomingClassLoader);

    AccountManager getAccountManager();

    void setAccountManager(AccountManager accountManager);

    GroupRecordManager getGroupRecordManager();

    void setGroupRecordManager(GroupRecordManager groupRecordManager);

    ReportMessageManager getReportMessageManager();

    void setReportMessageManager(ReportMessageManager reportMessageManager);

    ReceptionistManager getReceptionistManager();

    void setReceptionistManager(ReceptionistManager receptionistManager);

    Logger getLogger();

    LicenseManager getLicenseManager();

    void setLicenseManager(LicenseManager licenseManager);

    Scheduler getScheduler();

    void setScheduler(Scheduler scheduler);

    ResourceManager getResourceManager();

    void setResourceManager(ResourceManager resourceManager);

    File getLogDirectory();

    void setLogDirectory(File logDirectory);

    Serializer getCoreSerializer();

    Serializer getSerializer();

    void setSerializer(Serializer serializer);

    FileLoader getCoreFileLoader();

    FileSaver getFileSaver();

    void setFileSaver(FileSaver fileSaver);

    Optimizer getOptimizer();

    void setOptimizer(Optimizer optimizer);
}