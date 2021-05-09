package com.chuanwise.xiaoming.core.plugin;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.plugin.PluginManager;
import com.chuanwise.xiaoming.api.plugin.PluginProperty;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.JsonSerializerUtil;
import com.chuanwise.xiaoming.api.util.PluginLoaderUtil;
import com.chuanwise.xiaoming.core.error.ErrorMessageImpl;
import com.chuanwise.xiaoming.core.object.HostXiaomingObjectImpl;
import lombok.Getter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 小明的插件加载器加载插件的方法是，先收集所有插件文件，维护一张加载序列表，然后按照表逐一加载。
 * 首先加载没有前置插件的基本插件，然后加载已齐备所有前置插件的插件，不断执行直到两次加载结果相等。
 * 如果最终加载插件数和插件文件总数相等，所有插件均加载成功，否则有的插件加载失败。
 */
@Getter
public class PluginManagerImpl extends HostXiaomingObjectImpl implements PluginManager {
    final File directory;

    public PluginManagerImpl(XiaomingBot xiaomingBot, File directory) {
        super(xiaomingBot);
        this.directory = directory;
    }

    /**
     * 只要出现在插件文件夹并能被读取的，都被存放在这里
     */
    BidiMap<String, PluginProperty> existingPlugins = new DualHashBidiMap<>();

    /**
     * 载入了的插件
     */
    Set<XiaomingPlugin> loadedPlugins = new CopyOnWriteArraySet<>();

    /**
     * 启动成功了的插件
     */
    Set<XiaomingPlugin> enabledPlugins = new HashSet<>();

    @Override
    public boolean isExists(String name) {
        return existingPlugins.containsKey(name);
    }

    @Override
    public boolean isEnabled(String name) {
        for (XiaomingPlugin loadedPlugin : enabledPlugins) {
            if (Objects.equals(loadedPlugin.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLoaded(String name) {
        for (XiaomingPlugin plugin : loadedPlugins) {
            if (Objects.equals(plugin.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public XiaomingPlugin loadPlugin(XiaomingUser user,
                                     PluginProperty property) {
        final XiaomingPlugin plugin;
        final Object mainObject = property.get("main");
        final String pluginMainClassName;
        if (mainObject instanceof String) {
            pluginMainClassName = (String) mainObject;
        } else {
            user.sendError("找不到资源文件 plugin.json 中的插件主类名");
            return null;
        }

        final ClassLoader classLoader;
        final Class<?> pluginClass;

        // 扩展类加载器
        try {
            classLoader = PluginLoaderUtil.extendClassLoader(property.getFile(), Thread.currentThread().getContextClassLoader());
        } catch (Exception exception) {
            user.sendError("严重错误：无法扩展类加载器");
            getLog().error("无法扩展类加载器", exception);
            return null;
        }

        // 加载插件主类
        try {
            pluginClass = classLoader.loadClass(pluginMainClassName);
        } catch (ClassNotFoundException exception) {
            user.sendError("找不到插件主类：{}", pluginMainClassName);
            getXiaomingBot().getErrorMessageManager().addErrorMessage(new ErrorMessageImpl("找不到插件主类：" + pluginMainClassName));
            exception.printStackTrace();
            return null;
        }

        // 检查插件主类是否为 XiaomingPlugin 的子类
        if (!XiaomingPlugin.class.isAssignableFrom(pluginClass)) {
            user.sendError("插件主类：{}不是{}的子类，无法被小明加载", pluginMainClassName, XiaomingPlugin.class.getName());
            return null;
        }

        // 尝试调用默认构造函数
        try {
            plugin = (XiaomingPlugin) pluginClass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException exception) {
            user.sendError("无法访问插件主类：{}的构造函数，请为其准备一个默认的无参构造函数", pluginMainClassName);
            return null;
        } catch (Exception exception) {
            user.sendError("构造插件主类时出现异常：{}，请检查{}的默认的无参构造函数", pluginMainClassName, exception);
            exception.printStackTrace();
            return null;
        }

        plugin.setProperty(property);
        plugin.setClassLoader(classLoader);
        plugin.setXiaomingBot(getXiaomingBot());
        plugin.setDataFolder(new File(directory, plugin.getName()));
        plugin.setLogger(LoggerFactory.getLogger(plugin.getName()));
        return plugin;
    }

    @Override
    public void loadAllPlugins(XiaomingUser user) {
        int loadedPluginNumber = enabledPlugins.size();

        // 本次需要加载的插件
        pushAllUnloadLoader(user);

        if (existingPlugins.isEmpty()) {
            user.sendMessage("没有本次需要加载的插件");
            return;
        }

        // 获得本次要加载的插件列表
        Set<PluginProperty> properties = new HashSet<>();
        for (PluginProperty property : existingPlugins.values()) {
            if (!isLoaded(property.getName())) {
                properties.add(property);
            }
        }

        // 不断循环，直到无法再加载插件为止
        final Set<XiaomingPlugin> loadablePlugins = new HashSet<>();
        int lastLoadedPluginNumber;
        do {
            lastLoadedPluginNumber = loadablePlugins.size();
            for (PluginProperty property : properties) {
                boolean loadable = true;
                for (XiaomingPlugin plugin : loadablePlugins) {
                    if (Objects.equals(plugin.getName(), property.getName())) {
                        loadable = false;
                        break;
                    }
                }
                if (!isLoaded(property.getName()) && loadable) {
                    final XiaomingPlugin plugin = loadPlugin(user, property);
                    if (Objects.nonNull(plugin)) {
                        loadablePlugins.add(plugin);
                    }
                }
            }
        } while (lastLoadedPluginNumber != loadablePlugins.size());

        // 把所有插件都 load 一遍
        final Set<XiaomingPlugin> currentLoadedPlugins = new HashSet<>();
        for (XiaomingPlugin plugin : loadablePlugins) {
            try {
                plugin.onLoad();
                currentLoadedPlugins.add(plugin);
                loadedPlugins.add(plugin);
            } catch (Exception exception) {
                user.sendError("装载插件 {} 时出现异常：{}", plugin.getCompleteName(), exception);
                exception.printStackTrace();
            }
        }

        for (XiaomingPlugin plugin : currentLoadedPlugins) {
            try {
                plugin.onEnable();
                enabledPlugins.add(plugin);
            } catch (Exception exception) {
                user.sendError("启动插件 {} 时出现异常：{}", plugin.getCompleteName(), exception);
                exception.printStackTrace();
            }
        }

        user.sendMessage("成功装载并启动了 {} 个插件", enabledPlugins.size() - loadedPluginNumber);
    }

    @Override
    @Nullable
    public XiaomingPlugin getPlugin(final String pluginName) {
        for (XiaomingPlugin loadedPlugin : enabledPlugins) {
            if (Objects.equals(pluginName, loadedPlugin.getName())) {
                return loadedPlugin;
            }
        }
        return null;
    }

    @Override
    public boolean unloadPlugin(final XiaomingUser user,
                                final String pluginName) throws Exception {
        final XiaomingPlugin plugin = getPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            unloadPlugin(user, plugin);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void reloadAll(final XiaomingUser sender) throws Exception {
        for (XiaomingPlugin loadedPlugin : enabledPlugins) {
            reloadPlugin(sender, loadedPlugin);
        }
    }

    @Override
    public boolean reloadPlugin(final XiaomingUser sender,
                                final XiaomingPlugin plugin) throws Exception {
        return false;
    }

    @Override
    public void unloadPlugin(final XiaomingUser user,
                             final XiaomingPlugin plugin) throws Exception {
        // disablePlugin(user, plugin);
        enabledPlugins.remove(plugin.getName());
        getXiaomingBot().getInteractorManager().getPluginInteractors().remove(plugin);
        getXiaomingBot().getCommandManager().getPluginCommandExecutors().remove(plugin);
    }

    @Override
    @Nullable
    public PluginProperty getPluginProperty(JarFile jarFile) throws IOException {
        // 获取插件属性 plugin.json
        ZipEntry entry = jarFile.getEntry("plugin.json");
        if (Objects.isNull(entry)) {
            return null;
        }

        final PluginProperty pluginProperty;
        try (InputStream inputStream = jarFile.getInputStream(entry);) {
            pluginProperty = JsonSerializerUtil.getINSTANCE().readValue(inputStream, PluginPropertyImpl.class);
        }
        return pluginProperty;
    }

    @Override
    @Nullable
    public PluginProperty getPluginProperty(File pluginFile) throws IOException {
        return getPluginProperty(new JarFile(pluginFile));
    }

    @Override
    public void pushAllUnloadLoader(XiaomingUser user) {
        for (File pluginFile : directory.listFiles()) {
            if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                try {
                    final PluginProperty property = getPluginProperty(pluginFile);
                    if (Objects.isNull(property)) {
                        user.sendError("没有在插件文件 {} 中找到插件属性文件", pluginFile.getName());
                    } else {
                        property.setFile(pluginFile);
                        existingPlugins.put(property.getName(), property);
                    }
                } catch (IOException exception) {
                    getLog().error("插件属性文件错误", exception);
                }
            } else if (pluginFile.isFile()) {
                getLog().error("插件文件夹：" + directory.getAbsolutePath() + " 中出现了非 jar 类型的文件：" + pluginFile.getName());
            }
        }
    }
}