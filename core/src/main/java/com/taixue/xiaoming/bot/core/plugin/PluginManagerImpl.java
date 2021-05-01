package com.taixue.xiaoming.bot.core.plugin;

import com.alibaba.fastjson.JSON;
import com.taixue.xiaoming.bot.api.plugin.PluginManager;
import com.taixue.xiaoming.bot.api.plugin.PluginProperty;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.base.HostObjectImpl;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;
import com.taixue.xiaoming.bot.util.PathUtil;
import com.taixue.xiaoming.bot.util.PluginLoaderUtil;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 小明的插件加载器加载插件的方法是，先收集所有插件文件，维护一张加载序列表，然后按照表逐一加载。
 * 首先加载没有前置插件的基本插件，然后加载已齐备所有前置插件的插件，不断执行直到两次加载结果相等。
 * 如果最终加载插件数和插件文件总数相等，所有插件均加载成功，否则有的插件加载失败。
 */
public class PluginManagerImpl extends HostObjectImpl implements PluginManager {
    private final File directory = PathUtil.PLUGIN_DIR;

    /**
     * 只要出现在插件文件夹并能被读取的，都被存放在这里
     */
    private BidiMap<String, PluginProperty> pluginPropertyMap = new DualHashBidiMap<>();

    /**
     * 加载成功了的插件
     */
    private Set<XiaomingPlugin> loadedPlugins = new HashSet<>();

    /**
     * 判断插件是否加载成功
     */
    @Override
    public boolean isLoaded(final String pluginName) {
        for (XiaomingPlugin loadedPlugin : loadedPlugins) {
            if (Objects.equals(loadedPlugin.getName(), pluginName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得已经加载了的插件集合
     */
    @Override
    public Set<XiaomingPlugin> getLoadedPlugins() {
        return loadedPlugins;
    }

    /**
     * 通过 property 加载一个指定的插件
     * @return 加载好的插件实例。如果加载失败则为 null
     */
    @Override
    public XiaomingPlugin loadPlugin(final XiaomingUser sender,
                              final PluginProperty property) {
        final XiaomingPlugin plugin;
        final String pluginMainClassName = property.getMain();
        final File pluginJarFile = property.getPluginFile();

        final URLClassLoader urlClassLoader;
        final Class<?> pluginClass;

        // 扩展类加载器
        try {
            urlClassLoader = PluginLoaderUtil.extendURLClassLoader(pluginJarFile, ((URLClassLoader) XiaomingPlugin.class.getClassLoader()));
        } catch (Exception exception) {
            sender.sendError("无法扩展类加载器。请将此错误报告反馈给小明");
            exception.printStackTrace();
            return null;
        }

        // 加载插件主类
        try {
            pluginClass = urlClassLoader.loadClass(pluginMainClassName);
        } catch (ClassNotFoundException classNotFoundException) {
            sender.sendError("找不到插件主类：{}", pluginMainClassName);
            classNotFoundException.printStackTrace();
            return null;
        }

        // 检查插件主类是否为 XiaomingPlugin 的子类
        if (!XiaomingPlugin.class.isAssignableFrom(pluginClass)) {
            sender.sendError("插件主类：{}不是{}的子类，无法被小明加载", pluginMainClassName, XiaomingPlugin.class.getName());
            return null;
        }

        // 尝试调用默认构造函数
        try {
            plugin = (XiaomingPlugin) pluginClass.newInstance();
        } catch (IllegalAccessException exception) {
            sender.sendError("无法访问插件主类：{}的构造函数，请为其准备一个默认的无参构造函数", pluginMainClassName);
            return null;
        } catch (Exception exception) {
            sender.sendError("构造插件主类时出现异常：{}，请检查{}的默认的无参构造函数", pluginMainClassName, exception);
            exception.printStackTrace();
            return null;
        }

        plugin.setProperty(property);
        plugin.setClassLoader(urlClassLoader);
        return enablePlugin(sender, plugin) ? plugin : null;
    }

    /**
     * 尝试加载所有的插件
     */
    @Override
    public void loadAllPlugins(final XiaomingUser user) {
        // 本次需要加载的插件
        pushAllUnloadLoader(user);

        if (pluginPropertyMap.isEmpty()) {
            user.sendMessage("没有本次需要加载的插件");
            return;
        }

        // 不断循环，直到无法再加载插件为止
        int loadedPluginNumber = loadedPlugins.size();
        int lastLoadedPluginNumber;
        do {
            lastLoadedPluginNumber = loadedPlugins.size();
            for (PluginProperty value : ((Set<PluginProperty>) pluginPropertyMap.values())) {
                tryLoadPlugin(user, value);
            }
        } while (lastLoadedPluginNumber != loadedPlugins.size());

        user.sendMessage("成功加载了 {} 个插件", loadedPlugins.size() - loadedPluginNumber);
    }

    @Override
    public boolean tryLoadPlugin(final XiaomingUser sender,
                                 final PluginProperty property) {
        if (isLoaded(property.getName())) {
            return false;
        }
        boolean allFrontLoaded = true;
        final Object frontsObject = property.get("fronts");
        if (frontsObject instanceof List) {
            try {
                final List<String> fronts = (List<String>) frontsObject;
                for (String frontPluginName : fronts) {
                    if (!isLoaded(frontPluginName)) {
                        allFrontLoaded = false;
                        break;
                    }
                }
            } catch (ClassCastException exception) {
                exception.printStackTrace();
                sender.sendError("插件属性文件中的 fronts 应该是前置插件名数组");
                return false;
            }
        }

        if (allFrontLoaded) {
            final XiaomingPlugin xiaomingPlugin = loadPlugin(sender, property);
            if (Objects.nonNull(xiaomingPlugin)) {
                loadedPlugins.add(xiaomingPlugin);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获得加载好了的插件
     */
    @Override
    @Nullable
    public XiaomingPlugin getPlugin(final String pluginName) {
        for (XiaomingPlugin loadedPlugin : loadedPlugins) {
            if (Objects.equals(pluginName, loadedPlugin.getName())) {
                return loadedPlugin;
            }
        }
        return null;
    }

    /**
     * 卸载插件
     */
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

    /**
     * 重载所有插件
     * @param sender
     */
    @Override
    public void reloadAll(final XiaomingUser sender) throws Exception {
        for (XiaomingPlugin loadedPlugin : loadedPlugins) {
            reloadPlugin(sender, loadedPlugin);
        }
    }

    @Override
    public boolean reloadPlugin(final XiaomingUser sender,
                                final String pluginName) throws Exception {
        if (!isLoaded(pluginName)) {
            return false;
        }
        final PluginProperty property = (PluginProperty) pluginPropertyMap.get(pluginName);
        return Objects.nonNull(property) && reloadPlugin(sender, property);
    }

    @Override
    public boolean reloadPlugin(final XiaomingUser sender,
                                final XiaomingPlugin plugin) throws Exception {
        return reloadPlugin(sender, plugin.getProperty());
    }

    @Override
    public boolean reloadPlugin(final XiaomingUser user,
                                final PluginProperty property) throws Exception {
        final XiaomingPlugin plugin = property.getPlugin();
        unloadPlugin(user, plugin);
        return tryLoadPlugin(user, property);
    }

    @Override
    public void disablePlugin(final XiaomingUser user,
                              final XiaomingPlugin plugin) {
        try {
            user.sendMessage("正在卸载插件：{}", plugin.getName());
            plugin.onDisable();
            user.sendMessage("插件 {} 卸载完成", plugin.getName());
        } catch (Exception exception) {
            user.sendError("卸载插件 {} 时出现异常：{}", plugin.getName(), exception);
            exception.printStackTrace();
        }
    }

    @Override
    public boolean enablePlugin(final XiaomingUser user,
                                final XiaomingPlugin plugin) {
        try {
            plugin.onEnable();
            user.sendMessage("插件 {} 初始化完成", plugin.getName());
            return true;
        } catch (Exception exception) {
            user.sendError("初始化插件 {} 时出现异常：{}", plugin.getName(), exception);
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void unloadPlugin(final XiaomingUser user,
                             final XiaomingPlugin plugin) throws Exception {
        disablePlugin(user, plugin);
        loadedPlugins.remove(plugin.getName());
        plugin.unHookAll();
        getXiaomingBot().getInteractorManager().getPluginInteractors().remove(plugin);
        getXiaomingBot().getCommandManager().getPluginCommandExecutors().remove(plugin);
    }

    @Override
    @Nullable
    public PluginProperty getPluginProperty(final JarFile jarFile)
            throws IOException {
        // 获取插件属性 plugin.json
        ZipEntry entry = jarFile.getEntry("plugin.json");
        if (Objects.isNull(entry)) {
            return null;
        }

        final PluginProperty pluginProperty;
        try (InputStream inputStream = jarFile.getInputStream(entry);) {
            pluginProperty = JsonSerializerUtil.getInstance().readValue(inputStream, PluginProperty.class);
        }
        return pluginProperty;
    }

    @Override
    @Nullable
    public PluginProperty getPluginProperty(final File pluginFile)
            throws IOException {
        return getPluginProperty(new JarFile(pluginFile));
    }

    @Override
    public void pushAllUnloadLoader(final XiaomingUser user) {
        for (File pluginFile : directory.listFiles()) {
            if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                try {
                    final JarFile jarFile = new JarFile(pluginFile);
                    final JarEntry pluginPropertyEntry = jarFile.getJarEntry("plugin.json");
                    if (Objects.isNull(pluginPropertyEntry)) {
                        user.sendError("没有在插件文件 {} 中找到插件属性文件", pluginFile.getName());
                    } else {
                        final InputStream inputStream = jarFile.getInputStream(pluginPropertyEntry);
                        final PluginProperty property = JsonSerializerUtil.getInstance().readValue(inputStream, PluginPropertyImpl.class);
                        property.setPluginFile(pluginFile);
                        pluginPropertyMap.put(property.getName(), property);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (pluginFile.isFile()) {
                getLogger().error("插件文件夹：" + directory.getAbsolutePath() + " 中出现了非 jar 类型的文件：" + pluginFile.getName());
            }
        }
    }
}