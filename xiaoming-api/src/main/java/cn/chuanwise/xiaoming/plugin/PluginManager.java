package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.object.XiaomingObject;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 插件管理器是小明所有插件的管理者、加载者和卸载者
 *
 * @see Plugin
 * @date 2021年5月3日
 * @version 3.2
 * @author Chuanwise
 */
public interface PluginManager extends XiaomingObject, ModuleObject {
    /**
     * 判断插件是否存在
     * @param pluginName 插件名
     * @return 插件是否存在
     */
    boolean isExists(String pluginName);

    default boolean testStatus(String pluginName, Plugin.Status status) {
        final Plugin plugin = getPlugin(pluginName);
        return Objects.nonNull(plugin) && plugin.getStatus() == status;
    }

    /** 判断插件是否加载 */
    default boolean isEnabled(String pluginName) {
        return testStatus(pluginName, Plugin.Status.ENABLED);
    }

    /**
     * 获得一个已经加载了的插件
     * @param name 插件名
     * @return 该插件本体。如果插件未加载返回 {@code null}
     */
    Plugin getPlugin(String name);

    /** 判断插件是否加载 */
    default boolean isLoaded(String pluginName) {
        final Plugin plugin = getPlugin(pluginName);
        return Objects.nonNull(plugin) && plugin.getHandler().isLoaded();
    }

    /** 获取插件控制器 */
    PluginHandler getPluginHandler(String pluginName);

    Set<PluginHandler> getPluginHandlers();

    boolean addPluginHandler(String name, PluginHandler handler);

    default boolean addPluginHandler(PluginHandler handler) {
        return addPluginHandler(handler.getName(), handler);
    }

    default boolean addPluginHandlers(PluginHandler... handlers) {
        for (PluginHandler handler : handlers) {
            if (!addPluginHandler(handler)) {
                return false;
            }
        }
        return true;
    }

    default boolean addPluginHandlers(Collection<PluginHandler> handlers) {
        return addPluginHandlers(handlers.toArray(new PluginHandler[0]));
    }

    /** 加载一个插件 */
    boolean loadPlugin(PluginHandler handler);

    default boolean loadPlugin(String pluginName) {
        return loadPlugin(getPluginHandler(pluginName));
    }

    /** 尝试启动所有的插件 */
    void initialize();

    void flushPluginHandlers();

    /** 尝试启动插件 */
    boolean enablePlugin(Plugin plugin);

    default boolean enablePlugin(PluginHandler information) {
        return enablePlugin(information.getPlugin());
    }

    default boolean enablePlugin(String pluginName) {
        return enablePlugin(getPlugin(pluginName));
    }

    /** 关闭插件。插件仍然会在插件表中 */
    boolean disablePlugin(Plugin plugin);

    default boolean disablePlugin(String pluginName) {
        final Plugin plugin = getPlugin(pluginName);
        return Objects.nonNull(plugin) && disablePlugin(plugin);
    }

    default boolean disablePlugin(PluginHandler information) {
        return disablePlugin(information.getPlugin());
    }

    /** 重启插件 */
    default boolean reenablePlugin(Plugin plugin) {
        if (disablePlugin(plugin)) {
            return enablePlugin(plugin);
        }
        return false;
    }

    default boolean reenablePlugin(PluginHandler information) {
        return reenablePlugin(information.getPlugin());
    }

    default boolean reenablePlugin(String pluginName) {
        return reenablePlugin(getPlugin(pluginName));
    }

    /** 卸载插件 */
    boolean unloadPlugin(Plugin plugin);

    default boolean unloadPlugin(String pluginName) {
        return unloadPlugin(getPlugin(pluginName));
    }

    default boolean unloadPlugin(PluginHandler handler) {
        return unloadPlugin(handler.getPlugin());
    }

    /** 重新装载插件 */
    default boolean reloadPlugin(Plugin plugin) {
        if (Objects.isNull(plugin)) {
            return false;
        }
        final Plugin.Status beforeReload = plugin.getStatus();

        // 如果插件启动了，尝试关闭
        if (beforeReload == Plugin.Status.ENABLED && !disablePlugin(plugin)) {
            return false;
        }

        // 重新加载插件失败时返回
        final PluginHandler handler = plugin.getHandler();
        if (!unloadPlugin(handler) || !loadPlugin(handler)) {
            return false;
        }

        if (beforeReload == Plugin.Status.ENABLED) {
            return enablePlugin(handler);
        } else {
            return true;
        }
    }

    default boolean reloadPlugin(PluginHandler handler) {
        return reloadPlugin(handler.getPlugin());
    }

    default boolean reloadPlugin(String pluginName) {
        return reloadPlugin(getPlugin(pluginName));
    }

    File getDirectory();

    Map<String, Plugin> getPlugins();
}