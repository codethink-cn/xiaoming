package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.toolkit.map.TypePathGetter;
import cn.chuanwise.toolkit.map.PathSetter;
import cn.chuanwise.util.ArrayUtil;
import cn.chuanwise.util.LambdaUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public interface PluginHandler extends TypePathGetter, PathSetter {
    String DEFAULT_VERSION = "unknown";

    String MAIN_CLASS_NAME_PATH = "main";
    String SINGLE_AUTHOR_PATH = "author";
    String MULTIPLE_AUTHORS_PATH = "authors";
    String DEPENDS_PATH = "depends";
    String SOFT_DEPENDS_PATH = "soft-depends";
    String NAME_PATH = "name";
    String VERSION_PATH = "version";
    String USER_PERMISSIONS = "user-permissions";

    Map<String, Object> getValues();

    default String[] getUserPermissions() {
        return getAsStringArrayContainer(USER_PERMISSIONS).orElse(ArrayUtil.emptyArray(String.class));
    }

    default String getName() {
        return getStringContainer(NAME_PATH).orElseGet(() -> {
            final String jarFileName = getFile().getName();
            return jarFileName.substring(0, jarFileName.lastIndexOf('.'));
        });
    }

    default String getVersion() {
        return getStringContainer(VERSION_PATH).orElse(DEFAULT_VERSION);
    }

    default String getMainClassName() {
        return getString(MAIN_CLASS_NAME_PATH);
    }

    default String getSingleAuthor() {
        return getString(SINGLE_AUTHOR_PATH);
    }

    default String[] getMultipleAuthors() {
        return getStringArrayContainer(MULTIPLE_AUTHORS_PATH).orElse(ArrayUtil.emptyArray(String.class));
    }

    default String[] getDepends() {
        return getStringArrayContainer(DEPENDS_PATH).orElse(ArrayUtil.emptyArray(String.class));
    }

    default String[] getSoftDepends() {
        return getStringArrayContainer(SOFT_DEPENDS_PATH).orElse(ArrayUtil.emptyArray(String.class));
    }

    default boolean isSoftDepend(String pluginName) {
        return Arrays.asList(getSoftDepends()).contains(pluginName);
    }

    default boolean isSoftDepend(Plugin plugin) {
        return isSoftDepend(plugin.getName());
    }

    default boolean isSoftDepend(PluginHandler information) {
        return isSoftDepend(information.getName());
    }

    default boolean isDepend(String pluginName) {
        return Arrays.asList(getDepends()).contains(pluginName);
    }

    default boolean isDepend(Plugin plugin) {
        return isDepend(plugin.getName());
    }

    default boolean isDepend(PluginHandler information) {
        return isDepend(information.getName());
    }

    default boolean isAllDependsEnabled() {
        final PluginManager pluginManager = getPlugin().getXiaomingBot().getPluginManager();
        for (String depend : getDepends()) {
            if (!pluginManager.isEnabled(depend)) {
                return false;
            }
        }
        return true;
    }

    default boolean isAllSoftDependsEnabled() {
        final PluginManager pluginManager = getPlugin().getXiaomingBot().getPluginManager();
        for (String depend : getSoftDepends()) {
            if (pluginManager.isExists(depend)) {
                final PluginHandler handler = pluginManager.getPluginHandler(depend);
                if (Objects.isNull(handler.getPlugin()) ||
                        (handler.getPlugin().getStatus() != Plugin.Status.ENABLED &&
                                handler.getPlugin().getStatus() != Plugin.Status.ERROR)) {
                    return false;
                }
            }
        }
        return true;
    }

    default boolean isError() {
        return getPlugin().getStatus() == Plugin.Status.ERROR;
    }

    default boolean isEnabled() {
        final Plugin plugin = getPlugin();
        if (Objects.isNull(plugin)) {
            return false;
        }

        return plugin.getStatus() == Plugin.Status.ENABLED;
    }

    default boolean isLoaded() {
        final Plugin plugin = getPlugin();
        if (Objects.isNull(plugin)) {
            return false;
        }

        return plugin.getStatus() != Plugin.Status.CONSTRUCTED;
    }

    File getFile();

    void setFile(File file);

    Plugin getPlugin();

    void setPlugin(Plugin plugin);
}
