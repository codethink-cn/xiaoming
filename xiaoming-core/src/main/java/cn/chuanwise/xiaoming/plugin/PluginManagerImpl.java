package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.optional.ValueWithMessage;
import cn.chuanwise.toolkit.optional.SimpleValueWithMessage;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.classloader.XiaomingClassLoader;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 小明的插件管理器
 * @author Chuanwise
 */
public class PluginManagerImpl extends ModuleObjectImpl implements PluginManager {
    @Getter
    final File directory;

    public PluginManagerImpl(XiaomingBot xiaomingBot, File directory) {
        super(xiaomingBot);
        this.directory = directory;
    }

    /** 只要出现在插件文件夹并能被读取的，都被存放在这里 */
    BidiMap<String, PluginHandler> pluginHandlers = new DualHashBidiMap<>();

    /** 已经加载了的插件名录 */
    @Getter
    Map<String, Plugin> plugins = new ConcurrentHashMap<>();

    /** 刷新插件信息表 */
    @Override
    public void flushPluginHandlers() {
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        getLogger().info("正在刷新插件信息表");
        final BidiMap<String, PluginHandler> elderPluginHandlers = pluginHandlers;
        pluginHandlers = new DualHashBidiMap<>();

        // 对插件文件夹内所有的文件
        for (File file : directory.listFiles()) {
            // 如果是旧表中已经加载了的，不重复加载
            final PluginHandler lastLoadedHandler = CollectionUtil.first(elderPluginHandlers.values(), handler -> Objects.equals(file, handler.getFile()));
            if (Objects.nonNull(lastLoadedHandler)) {
                pluginHandlers.put(lastLoadedHandler.getName(), lastLoadedHandler);
                continue;
            }

            // 跳过文件夹
            if (!file.isFile()) {
                continue;
            }

            // 跳过非 jar 类型文件
            if (!file.getName().endsWith(".jar")) {
                getLogger().error("插件文件夹：" + directory.getAbsolutePath() + " 中出现了非 jar 类型的文件：" + file.getName());
                continue;
            }

            // 载入插件信息文件
            final PluginHandler handler;
            try {
                final ValueWithMessage<PluginHandler> valueOrMessage = loadPluginHandler(file);
                if (valueOrMessage.hasValue()) {
                    handler = valueOrMessage.getValue();
                } else {
                    getLogger().error("从文件 " + file.getAbsolutePath() + " 中加载插件信息错误：" + valueOrMessage.getMessage());
                    continue;
                }
            } catch (IOException exception) {
                getLogger().error("从文件 " + file.getAbsolutePath() + " 中加载插件信息时出现异常", exception);
                continue;
            }

            // 检查是否有重名插件
            final PluginHandler sameNamePluginHandler = pluginHandlers.get(handler.getName());
            if (Objects.nonNull(sameNamePluginHandler)) {
                getLogger().error("从文件 " + file.getAbsolutePath() + " 中加载插件时出现错误，因为小明已经加载插件名相同的插件了" +
                        "（从 " + sameNamePluginHandler.getFile().getAbsolutePath() + " 文件中）");
                continue;
            }

            // 添加插件表
            pluginHandlers.put(handler.getName(), handler);
        }
    }

    /** 从文件中加载插件的信息 */
    public ValueWithMessage<PluginHandler> loadPluginHandler(File file) throws IOException {
        if (!file.isFile() || !file.getName().endsWith(".jar")) {
            return new SimpleValueWithMessage<>("当前小明版本（" + XiaomingBot.COMPLETE_VERSION + "）下，只有 Jar 文件才能作为插件");
        }
        // 加载插件属性
        final JarFile jarFile = new JarFile(file);
        final ZipEntry informationEntry = jarFile.getEntry("plugin.json");
        if (Objects.isNull(informationEntry)) {
            return new SimpleValueWithMessage<>("找不到插件信息文件：plugin.json");
        }

        final InputStream inputStream = jarFile.getInputStream(informationEntry);
        final PluginHandler handler = new PluginHandlerImpl(getXiaomingBot().getCoreSerializer().deserialize(inputStream, StandardCharsets.UTF_8, HashMap.class));

        handler.setFile(file);
        return new SimpleValueWithMessage<>(handler);
    }

    @Override
    public Set<PluginHandler> getPluginHandlers() {
        return Collections.unmodifiableSet(pluginHandlers.values());
    }

    @Override
    public boolean addPlugin(String name, PluginHandler handler) {
        if (!pluginHandlers.containsKey(name)) {
            pluginHandlers.put(name, handler);
            return true;
        } else {
            return false;
        }
    }

    /** 获取插件信息 */
    @Override
    public PluginHandler getPluginHandler(String pluginName) {
        final PluginHandler information = pluginHandlers.get(pluginName);
        if (Objects.nonNull(information)) {
            return information;
        } else {
            flushPluginHandlers();
            return pluginHandlers.get(pluginName);
        }
    }

    @Override
    public Plugin getPlugin(String pluginName) {
        if (Objects.isNull(pluginName)) {
            return null;
        }
        return plugins.get(pluginName);
    }

    /**
     * 从插件信息中读取插件主类等信息，将其加载入插件加载表中
     * 期间会调用 {@link Plugin#onLoad()} 方法
     * @param handler 插件信息
     * @return 插件是否在本次加载了
     */
    @Override
    public boolean loadPlugin(PluginHandler handler)  {
        if (Objects.isNull(handler)) {
            return false;
        }
        final String mainClassName = handler.getMainClassName();

        // 不重复加载插件
        if (isLoaded(handler.getName())) {
            return false;
        }

        // 检查插件主类名
        if (Objects.isNull(mainClassName)) {
            getLogger().error("加载插件：" + handler.getName() + " 时，找不到资源文件 plugin.json 中的插件主类名。" +
                    "读取到的信息为：" + handler.getValues());
            return false;
        }

        getLogger().info("正在加载插件：" + handler.getName());

        // 扩展类加载器，加载插件主类
        final ClassLoader classLoader;
        final Class<?> pluginClass;

        // 扩展类加载器
        try {
            final XiaomingClassLoader xiaomingClassLoader = getXiaomingBot().getXiaomingClassLoader();

            final File file = handler.getFile();
            if (Objects.nonNull(file)) {
                xiaomingClassLoader.addURL(file.toURI().toURL());
            }

            classLoader = xiaomingClassLoader;
        } catch (Exception exception) {
            getLogger().error("无法扩展类加载器", exception);
            return false;
        }

        // 加载插件主类
        try {
            pluginClass = classLoader.loadClass(mainClassName);
        } catch (UnsupportedClassVersionError error) {
            getLogger().error("插件主类：" + mainClassName + " 是用更高版本 JDK 编译的，无法被低版本 JDK 加载。当前 JDK 版本：" + System.getProperty("java.version"));
            return false;
        } catch (ClassNotFoundException exception) {
            getLogger().error("找不到插件主类：" + mainClassName);
            return false;
        } catch (Throwable throwable) {
            getLogger().error("加载插件主类：" + mainClassName + " 时出现异常", throwable);
            return false;
        }

        // 检查插件主类是否为 Plugin 的子类
        if (!Plugin.class.isAssignableFrom(pluginClass)) {
            getLogger().error("插件主类：" + mainClassName + "不是 " + Plugin.class.getName() + " 的子类，无法被小明加载");
            return false;
        }

        // 准备获得插件实例
        Plugin plugin = null;

        // 检查是否存在非空的 INSTANCE
        try {
            // 获取属性
            final Field instanceField = pluginClass.getDeclaredField("INSTANCE");
            instanceField.setAccessible(true);

            // 获取实例
            final Object instanceValue = instanceField.get(pluginClass);

            // 获得实例引用
            if (Objects.nonNull(instanceValue) && pluginClass.isAssignableFrom(instanceValue.getClass())) {
                plugin = ((Plugin) instanceValue);
            } else {
                getLogger().error("插件主类 " + mainClassName + " 存在 INSTANCE 属性，但为空或不是该插件类类型。小明将尝试构造该插件主类的实例。");
            }
        } catch (Throwable ignored) {
        }

        // 如果没有，则尝试调用默认构造函数
        if (Objects.isNull(plugin)) {
            Constructor<?> constructor = null;
            try {
                constructor = pluginClass.getDeclaredConstructor();
            } catch (Exception exception) {
                getLogger().error("构造插件主类时出现异常，请检查 " + mainClassName + " 是否存在默认的无参构造函数、" +
                        "其访问权限是否为 public 等", exception);
                return false;
            }

            // 检查是否不存在
            if (Objects.isNull(constructor)) {
                getLogger().error("没有找到插件主类 " + mainClassName + " 的默认无参构造函数");
                return false;
            } else {
                constructor.setAccessible(true);
            }

            try {
                plugin = (Plugin) constructor.newInstance();
            } catch (IllegalAccessException ignored) {
            } catch (Exception exception) {
                getLogger().error("构造插件主类 " + mainClassName + " 时出现异常", exception);
                return false;
            }
        }

        // 一般来说不会走到这个分支
        if (Objects.isNull(plugin)) {
            getLogger().error("无法加载插件：" + handler.getName());
            return false;
        }

        // 设置相关属性
        plugin.setHandler(handler);
        plugin.setXiaomingBot(getXiaomingBot());
        plugin.setDataFolder(new File(directory, plugin.getName()));
        plugin.setLogger(LoggerFactory.getLogger(plugin.getName()));

        // load 插件
        try {
            plugin.setStatus(Plugin.Status.LOADING);
            plugin.onLoad();
            plugin.setStatus(Plugin.Status.LOADED);

            plugins.put(plugin.getName(), plugin);
            return true;
        } catch (Throwable throwable) {
            getLogger().error("加载插件：" + plugin.getName() + " 时出现异常", throwable);
            plugin.setStatus(Plugin.Status.ERROR);
            return false;
        }
    }

    @Override
    public boolean enablePlugin(Plugin plugin) {
        if (Objects.isNull(plugin) || plugin.getHandler().isEnabled()) {
            return false;
        }

        // 验证硬前置插件是否全部加载
        final PluginHandler handler = plugin.getHandler();
        boolean allDependsEnabled = handler.isAllDependsEnabled();
        if (!allDependsEnabled) {
            return false;
        }

        // 检查软前置插件是否是能加载的都加载了
        boolean triedBestToEnableSoftDepends = handler.isAllSoftDependsEnabled();
        if (!triedBestToEnableSoftDepends) {
            return false;
        }

        try {
            getLogger().info("正在启动插件：" + handler.getName());
            plugin.setStatus(Plugin.Status.ENABLING);
            plugin.onEnable();
            plugin.setStatus(Plugin.Status.ENABLED);
            return true;
        } catch (Throwable throwable) {
            getLogger().error("启动插件时出现异常", throwable);
            plugin.setStatus(Plugin.Status.ERROR);
            return false;
        }
    }

    @Override
    public boolean disablePlugin(Plugin plugin) {
        // 插件为空或插件没启动，都 false
        if (Objects.isNull(plugin)) {
            return false;
        }
        if (plugin.getStatus() != Plugin.Status.ENABLED) {
            return false;
        }

        final String pluginName = plugin.getName();
        getLogger().info("正在关闭插件：" + pluginName);

        // 对于每一个插件
        for (Plugin xiaomingPlugin : plugins.values()) {
            // 只检查本插件之外的启动了的插件
            if (xiaomingPlugin == plugin) {
                continue;
            }
            if (xiaomingPlugin.getStatus() != Plugin.Status.ENABLED) {
                continue;
            }

            // 如果这个插件和本插件有依赖
            // 如果是软依赖，回调通知
            if (xiaomingPlugin.getHandler().isSoftDepend(pluginName)) {
                xiaomingPlugin.onDisableSoftDepend(plugin);
            }

            // 如果是强依赖，则关闭插件
            if (xiaomingPlugin.getHandler().isDepend(pluginName)) {
                getLogger().info("需要关闭的插件：" + pluginName + " 是插件：" + xiaomingPlugin.getName() + " 的强依赖，需连带关闭");
                disablePlugin(xiaomingPlugin);
            }
        }

        try {
            plugin.setStatus(Plugin.Status.DISABLING);
            plugin.onDisable();

            // 进行扫尾工作，卸载事件监听器等东西
            getXiaomingBot().getEventManager().unregisterListeners(plugin);
            getXiaomingBot().getInteractorManager().unregisterPlugin(plugin);
            getXiaomingBot().getLanguageManager().unregisterPlugin(plugin);

            return true;
        } catch (Throwable throwable) {
            getLogger().error("关闭插件：" + pluginName + " 时出现异常", throwable);
            return false;
        } finally {
            plugin.setStatus(Plugin.Status.DISABLED);
        }
    }

    @Override
    public boolean unloadPlugin(Plugin plugin) {
        if (Objects.isNull(plugin)) {
            return false;
        }
        if (!plugin.getHandler().isLoaded()) {
            return false;
        }
        if (plugin.getHandler().isEnabled() && !disablePlugin(plugin)) {
            return false;
        }

        getLogger().info("正在卸载插件：" + plugin.getName());
        if (plugin.getHandler().isEnabled()) {
            if (!disablePlugin(plugin)) {
                return false;
            }
        }

        try {
            plugin.setStatus(Plugin.Status.UNLOADING);
            plugin.onUnload();
            plugin.setStatus(Plugin.Status.UNLOADED);
            plugins.remove(plugin.getName());
            return true;
        } catch (Throwable exception) {
            getLogger().error("卸载插件 " + plugin.getName() + " 时出现异常", exception);
            plugin.setStatus(Plugin.Status.ERROR);
            return false;
        }
    }

    @Override
    public void initialize() {
        ConditionUtil.checkState(getXiaomingBot().getStatus() == XiaomingBot.Status.ENABLING,
                "can not call the initialize method when xiaoming is not enabling");
        tryLoadPlugins();
        tryEnablePlugins();
    }

    protected int tryLoadPlugins() {
        // 刷新插件表
        if (pluginHandlers.isEmpty()) {
            flushPluginHandlers();
        }

        // 获得需要加载的插件
        final Set<PluginHandler> informationRequireLoad = CollectionUtil.filter(pluginHandlers.values(), information -> Objects.isNull(information.getPlugin()));

        // 对每一个这样的插件都加载一次
        // 首先是 load
        // 为了异步加载并等待结果，设置了 loadFutures
        final List<Future<Boolean>> loadFutures = new ArrayList<>(informationRequireLoad.size());
        for (PluginHandler information : informationRequireLoad) {
            final Future<Boolean> loadFuture = getXiaomingBot().getScheduler().run(() -> loadPlugin(information));
            loadFutures.add(loadFuture);
        }

        // 等待所有的都加载完成
        int loadedPluginNumber = 0;
        for (Future<Boolean> loadFuture : loadFutures) {
            try {
                if (loadFuture.get()) {
                    loadedPluginNumber++;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return loadedPluginNumber;
    }

    protected int tryEnablePlugins() {
        // 不断循环，直到无法再启动插件为止
        final Set<PluginHandler> informationRequireEnable = CollectionUtil.filter(pluginHandlers.values(),
                information -> Objects.nonNull(information.getPlugin()) && (information.getPlugin().getStatus() == Plugin.Status.LOADED));
        int enabledPluginNumber = 0;
        int thisTimeEnabledPluginNumber = 0;
        do {
            thisTimeEnabledPluginNumber = 0;
            for (PluginHandler information : informationRequireEnable) {
                if (enablePlugin(information)) {
                    thisTimeEnabledPluginNumber++;
                }
            }
            enabledPluginNumber += thisTimeEnabledPluginNumber;
        } while (thisTimeEnabledPluginNumber != 0);

        return enabledPluginNumber;
    }

    @Override
    public boolean isExists(String pluginName) {
        return pluginHandlers.containsKey(pluginName);
    }
}