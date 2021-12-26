package cn.chuanwise.xiaoming.bot;

import cn.chuanwise.api.StatusHolder;
import cn.chuanwise.xiaoming.account.AccountManager;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.configuration.Statistician;
import cn.chuanwise.xiaoming.contact.ContactManager;
import cn.chuanwise.xiaoming.group.GroupInformationManager;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.listener.EventManager;
import cn.chuanwise.xiaoming.permission.PermissionService;
import cn.chuanwise.xiaoming.schedule.FileSaver;
import cn.chuanwise.xiaoming.interactor.InteractorManager;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.resource.ResourceManager;
import cn.chuanwise.xiaoming.optimize.Optimizer;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import cn.chuanwise.toolkit.preservable.loader.FileLoader;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.xiaoming.schedule.Scheduler;
import net.mamoe.mirai.Bot;
import org.slf4j.Logger;

import java.io.File;

/**
 * 小明机器人本体
 * @author Chuanwise
 */
public interface XiaomingBot extends StatusHolder<XiaomingBot.Status> {
    enum Status {
        ENABLING,
        ENABLED,
        DISABLING,
        DISABLED
    }

    String VERSION = "4.2-rel";

    String SPONSOR = "Chuanwise";
    String GITHUB = "https://github.com/Chuanwise/xiaoming-bot";
    String GROUP = "1028959718";
    String DEVELOPMENT_DOCUMENT = "http://chuanwise.cn:10074/#/development/";

    default long getCode() {
        return getMiraiBot().getId();
    }

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    File getWorkingDirectory();

    void setWorkingDirectory(File workingDirectory);

    File getReportDirectory();

    void setReportDirectory(File workingDirectory);

    Bot getMiraiBot();

    void setMiraiBot(Bot bot);

    FileLoader getFileLoader();

    void setFileLoader(FileLoader fileLoader);

    PermissionService getPermissionService();

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

    void start();

    void load();

    boolean load(String name);

    void stop();

    default boolean isDisabled() {
        return getStatus() == Status.DISABLED;
    }

    default boolean isEnabled() {
        return getStatus() == Status.ENABLED;
    }

    Configuration getConfiguration();

    void setConfiguration(Configuration configuration);

    File getConfigurationDirectory();

    void setConfigurationDirectory(File configurationDirectory);

    Statistician getStatistician();

    void setStatistician(Statistician statistician);

    ConsoleXiaomingUser getConsoleXiaomingUser();

    void setConsoleXiaomingUser(ConsoleXiaomingUser consoleXiaomingUser);

    XiaomingClassLoader getXiaomingClassLoader();

    void setXiaomingClassLoader(XiaomingClassLoader xiaomingClassLoader);

    AccountManager getAccountManager();

    void setAccountManager(AccountManager accountManager);

    GroupInformationManager getGroupInformationManager();

    void setGroupInformationManager(GroupInformationManager groupInformationManager);

    ReceptionistManager getReceptionistManager();

    void setReceptionistManager(ReceptionistManager receptionistManager);

    Logger getLogger();

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