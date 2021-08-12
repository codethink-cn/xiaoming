package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;

import java.util.List;
import java.util.Map;

public interface CorePluginSeparated<T> extends ModuleObject {
    void register(T value, XiaomingPlugin plugin);

    void disable(XiaomingPlugin plugin);

    void denyCoreRegister();

    Map<XiaomingPlugin, List<T>> getRegisteredByPlugin();
}
