package com.taixue.xiaoming.bot.core.plugin;

import com.taixue.xiaoming.bot.api.plugin.PluginProperty;
import com.taixue.xiaoming.bot.api.plugin.XiaomingPlugin;
import com.taixue.xiaoming.bot.util.JsonSerializerUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class PluginPropertyImpl extends HashMap<String, Object> implements PluginProperty {
    private transient File pluginFile;
    private transient XiaomingPlugin plugin;

    @Override
    public XiaomingPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(XiaomingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getPluginFile() {
        return pluginFile;
    }

    @Override
    public void setPluginFile(File pluginFile) {
        this.pluginFile = pluginFile;
    }

    @Override
    public String getMain() {
        final Object o = get("main");
        return Objects.nonNull(o) && o instanceof String ? ((String) o) : null;
    }

    @Override
    public String getName() {
        final String name = getPluginFile().getName().substring(0, getPluginFile().getName().lastIndexOf('.'));
        try {
            return (String) getOrDefault("name", name);
        } catch (Exception exception) {
            exception.printStackTrace();
            return name;
        }
    }

    @Override
    public String getVersion() {
        try {
            return (String) getOrDefault("version", "unknown");
        } catch (Exception exception) {
            exception.printStackTrace();
            return "unknown";
        }
    }

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        final Object o = get(key);
        if (Objects.nonNull(o)) {
            final T t = JsonSerializerUtil.getInstance().convert(o, clazz);
            replace(key, t);
            return t;
        } else {
            return null;
        }
    }
}
