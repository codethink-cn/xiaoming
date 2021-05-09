package com.chuanwise.xiaoming.api.plugin;

import java.util.Map;

public interface PluginProperty extends Map<String, Object> {
    String getName();

    String getVersion();

    java.io.File getFile();

    void setFile(java.io.File file);
}
