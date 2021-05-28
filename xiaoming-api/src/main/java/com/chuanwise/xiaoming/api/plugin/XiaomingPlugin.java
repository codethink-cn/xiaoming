package com.chuanwise.xiaoming.api.plugin;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.api.util.FileUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * 小明插件本体
 * @author Chuanwise
 */
public interface XiaomingPlugin extends XiaomingObject {
    String CONFIGURATION_FILE_NAME = "congurations.json";
    /**
     * 获取插件名
     * @return
     */
    default String getName() {
        return getProperty().getName();
    }

    default String getAlias() {
        final Object alias = getProperty().get("alias");
        if (alias instanceof String) {
            return ((String) alias);
        } else if (alias instanceof List && !((List<?>) alias).isEmpty()) {
            final Object firstObject = ((List<?>) alias).get(0);
            if (firstObject instanceof String) {
                return ((String) firstObject);
            }
        }
        return getName();
    }

    /**
     * 获取插件名
     * @return
     */
    default String getVersion() {
        return getProperty().getVersion();
    }

    default String getCompleteName() {
        return getName() + "（" + getVersion() + "）";
    }

    default void onEnable() {}

    default void onDisable() {}

    default void onLoad() {}

    default void onUnload() {}

    PluginProperty getProperty();

    Logger getLog();

    void setLog(Logger log);

    void setProperty(PluginProperty property);

    void setClassLoader(ClassLoader classLoader);

    File getDataFolder();

    void setDataFolder(File folder);

    ClassLoader getClassLoader();

    default File getConfigurationFile() {
        return new File(getDataFolder(), CONFIGURATION_FILE_NAME);
    }

    default boolean copyResourceTo(String path, File to) throws IOException {
        return FileUtil.copyResource(getClassLoader(), path, to);
    }

    default boolean copyDefaultConfiguration() throws IOException {
        return copyResourceTo(CONFIGURATION_FILE_NAME, getConfigurationFile());
    }

    default <T extends Preservable<File>> T loadConfigurationAs(Class<T> clazz) {
        return loadFileAs(clazz, getConfigurationFile());
    }

    default <T extends Preservable<File>> T loadFileAs(Class<T> clazz, File file) {
        return getXiaomingBot().getFilePreservableFactory().load(clazz, file);
    }

    default <T extends Preservable<File>> T loadFileAs(Class<T> clazz, String fileName) {
        return loadFileAs(clazz, new File(getDataFolder(), fileName));
    }

    default <T extends Preservable<File>> T loadConfigurationOrProduce(Class<T> clazz, Supplier<T> supplier) {
        return loadFileOrProduce(clazz, getConfigurationFile(), supplier);
    }

    default <T extends Preservable<File>> T loadFileOrProduce(Class<T> clazz, File file, Supplier<T> supplier) {
        return getXiaomingBot().getFilePreservableFactory().loadOrProduce(clazz, file, supplier);
    }

    default <T extends Preservable<File>> T loadFileOrProduce(Class<T> clazz, String fileName, Supplier<T> supplier) {
        return loadFileOrProduce(clazz, new File(getDataFolder(), fileName), supplier);
    }

    default boolean onMessage(XiaomingUser user) {
        return false;
    }
}
