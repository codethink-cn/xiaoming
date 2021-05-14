package com.chuanwise.xiaoming.api.object;

import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;

/**
 * 插件主体对象
 * @author Chuanwise
 */
public interface PluginObject extends XiaomingObject {
    XiaomingPlugin getPlugin();

    void setPlugin(XiaomingPlugin plugin);
}