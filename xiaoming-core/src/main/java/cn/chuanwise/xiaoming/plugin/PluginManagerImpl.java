package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.report.ReportMessageImpl;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 小明的插件管理器
 * @author Chuanwise
 */
@Getter
public class PluginManagerImpl extends ModuleObjectImpl implements PluginManager {
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
    public boolean loadPlugin(XiaomingUser user, PluginProperty property)  {
        final XiaomingPlugin plugin;
        final Object mainObject = property.get("main");
        final String pluginMainClassName;

        // 不重复加载插件
        if (isLoaded(property.getName())) {
            return false;
        }

        if (mainObject instanceof String) {
            pluginMainClassName = (String) mainObject;
        } else {
            user.sendError("找不到资源文件 plugin.json 中的插件主类名");
            return false;
        }

        final ClassLoader classLoader;
        final Class<?> pluginClass;

        // 扩展类加载器
        try {
            final XiaomingClassLoader xiaomingClassLoader = getXiaomingBot().getXiaomingClassLoader();
            xiaomingClassLoader.addURL(property.getFile().toURI().toURL());
            classLoader = xiaomingClassLoader;
        } catch (Exception exception) {
            user.sendError("严重错误：无法扩展类加载器");
            getLog().error("无法扩展类加载器", exception);
            return false;
        }


        // 加载插件主类
        try {
            pluginClass = classLoader.loadClass(pluginMainClassName);
        } catch (Throwable throwable) {
            user.sendError("找不到插件主类：{}", pluginMainClassName);
            getXiaomingBot().getReportMessageManager().addMessage(new ReportMessageImpl("找不到插件主类：" + pluginMainClassName));
            throwable.printStackTrace();
            return false;
        }

        // 检查插件主类是否为 XiaomingPlugin 的子类
        if (!XiaomingPlugin.class.isAssignableFrom(pluginClass)) {
            user.sendError("插件主类：{}不是{}的子类，无法被小明加载", pluginMainClassName, XiaomingPlugin.class.getName());
            return false;
        }

        // 尝试调用默认构造函数
        Constructor<?> constructor = null;
        try {
            constructor = pluginClass.getDeclaredConstructor();
        } catch (Exception exception) {
            user.sendError("构造插件主类时出现异常：{}，请检查{}是否存在默认的无参构造函数、其访问权限是否为 public 等", pluginMainClassName, exception);
            getLog().error("构造插件主类时出现异常", exception);
            return false;
        }

        if (Objects.isNull(constructor)) {
            user.sendError("没有找到插件主类 {} 的默认无参构造函数", pluginMainClassName);
            getLog().error("没有找到插件主类 {} 的默认无参构造函数", pluginMainClassName);
            return false;
        }

        try {
            plugin = (XiaomingPlugin) constructor.newInstance();
        } catch (IllegalAccessException exception) {
            user.sendError("权限不足，无法访问插件主类：{} 的构造函数", pluginMainClassName);
            return false;
        } catch (Exception exception) {
            user.sendError("构造插件主类时出现异常：{}", pluginMainClassName, exception);
            getLog().error("构造插件主类时出现异常", exception);
            return false;
        }

        plugin.setProperty(property);
        plugin.setXiaomingBot(getXiaomingBot());
        plugin.setDataFolder(new File(directory, plugin.getName()));
        plugin.setLog(LoggerFactory.getLogger(plugin.getName()));

        return loadPlugin(user, plugin);
    }

    @Override
    public void loadAllPlugins(XiaomingUser user) {
        int loadedPluginNumber = enabledPlugins.size();

        // 如果还没有更新过存在插件列表就更新一下
        if (existingPlugins.isEmpty()) {
            flushPluginMap(user);
        }

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
                if (loadPlugin(user, property)) {
                    enablePlugin(user, property.getPlugin());
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
    public void flushPluginMap(XiaomingUser user) {
        existingPlugins.clear();
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

    @Override
    public PluginProperty getPluginProperty(JarFile jarFile) throws IOException {
        // 获取插件属性 plugin.json
        ZipEntry entry = jarFile.getEntry("plugin.json");
        if (Objects.isNull(entry)) {
            return null;
        }

        final PluginProperty pluginProperty;
        try (InputStream inputStream = jarFile.getInputStream(entry);) {
            pluginProperty = getXiaomingBot().getCoreSerializer().deserialize(inputStream, PluginPropertyImpl.class);
        }
        return pluginProperty;
    }
}