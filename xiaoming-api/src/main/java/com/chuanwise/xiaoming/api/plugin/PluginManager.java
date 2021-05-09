package com.chuanwise.xiaoming.api.plugin;

import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import org.apache.commons.collections4.BidiMap;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

public interface PluginManager extends XiaomingObject, HostXiaomingObject {
    /**
     * 判断插件是否存在
     * @param name
     * @return
     */
    boolean isExists(String name);

    /**
     * 判断插件是否加载
     */
    boolean isEnabled(String name);

    /**
     * 判断插件是否启动
     */
    boolean isLoaded(String name);

    /**
     * 通过 {@code property} 获得一个指定的插件实例
     * @return 插件实例，如果加载失败返回 {@code null}
     */
    XiaomingPlugin loadPlugin(XiaomingUser user,
                              PluginProperty property);

    /**
     * 尝试加载所有的插件
     */
    void loadAllPlugins(XiaomingUser user);

    /**
     * 获得加载好了的插件
     */
    @Nullable XiaomingPlugin getPlugin(String pluginName);

    /**
     * 卸载插件
     */
    boolean unloadPlugin(XiaomingUser user,
                         String pluginName) throws Exception;

    /**
     * 重载所有插件
     * @param sender
     */
    void reloadAll(XiaomingUser sender) throws Exception;

    boolean reloadPlugin(XiaomingUser sender,
                         XiaomingPlugin plugin) throws Exception;

    void unloadPlugin(XiaomingUser user,
                      XiaomingPlugin plugin) throws Exception;

    PluginProperty getPluginProperty(JarFile jarFile) throws IOException;

    PluginProperty getPluginProperty(File pluginFile) throws IOException;

    void pushAllUnloadLoader(XiaomingUser user);

    File getDirectory();

    BidiMap<String, PluginProperty> getExistingPlugins();

    Set<XiaomingPlugin> getLoadedPlugins();

    Set<XiaomingPlugin> getEnabledPlugins();
}