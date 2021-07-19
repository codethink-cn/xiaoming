package cn.chuanwise.xiaoming.api.plugin;

import cn.chuanwise.xiaoming.api.object.ModuleObject;
import cn.chuanwise.xiaoming.api.object.XiaomingObject;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import org.apache.commons.collections4.BidiMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * 插件管理器是小明所有插件的管理者、加载者和卸载者
 *
 * @see XiaomingPlugin
 * @date 2021年5月3日
 * @version 3.2
 * @author Chuanwise
 */
public interface PluginManager extends XiaomingObject, ModuleObject {
    /**
     * 判断插件是否存在
     * @param name 插件名
     * @return 插件是否存在
     */
    default boolean isExists(String name) {
        return getExistingPlugins().containsKey(name);
    }

    /**
     * 获取存在的插件的属性
     * @param name 插件名
     * @return 该插件的属性。如果无此插件返回 {@code null}
     */
    default PluginProperty getPluginProperty(String name) {
        for (Map.Entry<String, PluginProperty> entry : getExistingPlugins().entrySet()) {
            if (Objects.equals(entry.getKey(), name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 获得已经启动了的插件
     * @param name 插件名
     * @return 该插件本体。如果插件未启动返回 {@code null}
     */
    default XiaomingPlugin getEnabledPlugin(String name) {
        for (XiaomingPlugin plugin : getEnabledPlugins()) {
            if (Objects.equals(plugin.getName(), name)) {
                return plugin;
            }
        }
        return null;
    }

    /**
     * 判断插件是否加载
     */
    default boolean isEnabled(String name) {
        return Objects.nonNull(getEnabledPlugin(name));
    }

    /**
     * 获得一个已经加载了的插件
     * @param name 插件名
     * @return 该插件本体。如果插件未加载返回 {@code null}
     */
    default XiaomingPlugin getLoadedPlugin(String name) {
        for (XiaomingPlugin plugin : getLoadedPlugins()) {
            if (Objects.equals(plugin.getName(), name)) {
                return plugin;
            }
        }
        return null;
    }

    /**
     * 判断插件是否启动
     * @param name 插件名
     * @return 插件是否启动
     */
    default boolean isLoaded(String name) {
        return Objects.nonNull(getLoadedPlugin(name));
    }

    /**
     * 加载一个指定的插件实例
     * @return 插件实例，如果加载失败返回 {@code null}
     */
    boolean loadPlugin(XiaomingUser user, PluginProperty property);

    /**
     * 加载一个插件
     * @param user 用户名
     * @param pluginName 插件名
     * @return 加载上的插件实例
     */
    default boolean loadPlugin(XiaomingUser user, String pluginName) {
        final XiaomingPlugin loadedPlugin = getLoadedPlugin(pluginName);
        if (Objects.nonNull(loadedPlugin)) {
            user.sendError("插件{}已经被加载了", pluginName);
            return false;
        } else {
            final PluginProperty pluginProperty = getPluginProperty(pluginName);
            if (Objects.nonNull(pluginProperty)) {
                return loadPlugin(user, pluginProperty);
            } else {
                user.sendError("找不到需要加载的插件：{}", pluginName);
                return false;
            }
        }
    }

    default boolean loadPlugin(XiaomingUser user, XiaomingPlugin plugin) {
        try {
            plugin.onLoad();
            getLoadedPlugins().add(plugin);
            user.sendMessage("插件 {} 加载成功", plugin.getCompleteName());
            return true;
        } catch (Exception exception) {
            user.sendError("加载插件时出现异常：{}", exception);
            getLog().error("加载插件时出现异常", exception);
            return false;
        }
    }

    /**
     * 尝试加载所有的插件
     */
    void loadAllPlugins(XiaomingUser user);

    /**
     * 卸载插件
     */
    default boolean unloadPlugin(XiaomingUser user, String pluginName) {
        final XiaomingPlugin plugin = getLoadedPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            final XiaomingPlugin enabledPlugin = getEnabledPlugin(pluginName);
            if (Objects.isNull(enabledPlugin) || disablePlugin(user, plugin)) {
                unloadPlugin(user, plugin);
                return true;
            } else {
                return false;
            }
        } else {
            user.sendError("找不到需要卸载的插件：{}", pluginName);
            return false;
        }
    }

    default boolean reenablePlugin(XiaomingUser user, String pluginName) {
        final XiaomingPlugin plugin = getEnabledPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            return reenablePlugin(user, plugin);
        } else {
            user.sendError("找不到需要重启的插件：{}", pluginName);
            return false;
        }
    }

    default boolean enablePlugin(XiaomingUser user, String pluginName) {
        if (isEnabled(pluginName)) {
            user.sendError("插件{}已经启动了", pluginName);
            return false;
        } else {
            XiaomingPlugin loadedPlugin = getLoadedPlugin(pluginName);
            if (Objects.isNull(loadedPlugin)) {
                if (loadPlugin(user, pluginName)) {
                    loadedPlugin = getLoadedPlugin(pluginName);
                    user.sendWarning("插件{}还没有载入，现在已经载入成功了", pluginName);
                } else {
                    user.sendError("找不到插件：{}", pluginName);
                    return false;
                }
            }

            return enablePlugin(user, loadedPlugin);
        }
    }

    default boolean enablePlugin(XiaomingUser user, XiaomingPlugin plugin) {
        try {
            plugin.onEnable();
            getEnabledPlugins().add(plugin);
            user.sendMessage("插件{}启动成功", plugin.getCompleteName());
            return true;
        } catch (Exception exception) {
            user.sendError("启动插件时出现异常：{}", exception);
            getLog().error("启动插件时出现异常", exception);
            return false;
        }
    }

    default boolean reenablePlugin(XiaomingUser user, XiaomingPlugin plugin) {
        if (disablePlugin(user, plugin)) {
            return enablePlugin(user, plugin);
        } else {
            return false;
        }
    }

    default boolean disablePlugin(XiaomingUser user, String pluginName) {
        final XiaomingPlugin plugin = getEnabledPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            return disablePlugin(user, plugin);
        } else {
            user.sendError("找不到需要关闭的插件：{}", pluginName);
            return false;
        }
    }

    default boolean disablePlugin(XiaomingUser user, XiaomingPlugin plugin) {
        try {
            plugin.onDisable();
            getEnabledPlugins().remove(plugin);
            user.sendMessage("插件{}关闭成功", plugin.getCompleteName());
            return true;
        } catch (Exception exception) {
            user.sendError("关闭插件时出现异常：{}", exception);
            getLog().error("关闭插件时出现异常", exception);
            return false;
        }
    }

    /**
     * 重载所有插件
     * @param user
     */
    default void reloadAll(XiaomingUser user) {
        for (XiaomingPlugin loadedPlugin : getEnabledPlugins()) {
            reloadPlugin(user, loadedPlugin);
        }
    }

    default boolean reloadPlugin(XiaomingUser user, String pluginName) {
        final XiaomingPlugin plugin = getLoadedPlugin(pluginName);
        if (Objects.nonNull(plugin)) {
            return reloadPlugin(user, plugin);
        } else {
            user.sendError("找不到需要重启的插件：{}", pluginName);
            return false;
        }
    }

    default boolean reloadPlugin(XiaomingUser user, XiaomingPlugin plugin) {
        final String pluginName = plugin.getName();
        if (unloadPlugin(user, plugin)) {
            return Objects.nonNull(loadPlugin(user, pluginName));
        } else {
            return false;
        }
    }

    default boolean unloadPlugin(XiaomingUser user, XiaomingPlugin plugin) {
        getLoadedPlugins().remove(plugin);
        try {
            plugin.onUnload();
            // 卸载交互器
            getXiaomingBot().getInteractorManager().getPluginInteractors().remove(plugin);
            // 卸载监听器
            getXiaomingBot().getEventManager().remove(plugin);
            user.sendMessage("插件{}卸载成功", plugin.getCompleteName());
            return true;
        } catch (Exception exception) {
            user.sendError("卸载插件时出现异常：{}", exception);
            getLog().error("卸载插件时出现异常", exception);
            return false;
        }
    }

    PluginProperty getPluginProperty(JarFile jarFile) throws IOException;

    default PluginProperty getPluginProperty(File pluginFile) throws IOException {
        return getPluginProperty(new JarFile(pluginFile));
    }

    void flushPluginMap(XiaomingUser user);

    File getDirectory();

    BidiMap<String, PluginProperty> getExistingPlugins();

    Set<XiaomingPlugin> getLoadedPlugins();

    Set<XiaomingPlugin> getEnabledPlugins();
}