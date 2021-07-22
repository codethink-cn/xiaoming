package cn.chuanwise.xiaoming.plugin;

import java.io.File;
import java.util.Map;

public interface PluginProperty extends Map<String, Object> {
    String getName();

    String getVersion();

    File getFile();

    void setFile(java.io.File file);

    XiaomingPlugin getPlugin();

    void setPlugin(XiaomingPlugin plugin);
}
