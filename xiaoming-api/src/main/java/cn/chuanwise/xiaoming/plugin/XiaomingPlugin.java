package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.utility.ResourceUtility;
import cn.chuanwise.xiaoming.language.Language;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.toolkit.preservable.Preservable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * 小明插件主类
 * 插件主类是小明加载插件的唯一入口。通过 {@link cn.chuanwise.xiaoming.classloader.XiaomingClassLoader} 加载插件类后，会检查该类是否是
 * 本接口的实现。如果是，才会继续通过多态性调用本接口的 {@link XiaomingPlugin#onLoad()} 和 {@link XiaomingPlugin#onEnable()} 以
 * 启动插件。
 * {@link XiaomingPlugin#onLoad()} 的执行顺序是随机的，但 {@link XiaomingPlugin#onEnable()} 的执行顺序必然是一个前置插件关系的拓
 * 扑序列。
 *
 * @see PluginManager
 * @date 2021年5月3日
 * @version 3.1
 * @author Chuanwise
 */
public interface XiaomingPlugin extends XiaomingObject {
    /** 配置文件名 */
    String CONFIGURATION_FILE_NAME = "configurations.json";

    /** 获取插件名。如果插件属性中没有插件名，以 {@code jar} 文件名作为插件名 */
    default String getName() {
        return getProperty().getName();
    }

    /** 获取插件别名 */
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

    /** 获取插件版本号 */
    default String getVersion() {
        return getProperty().getVersion();
    }

    /** 获得插件名（版本） */
    default String getCompleteName() {
        return getName() + "（" + getVersion() + "）";
    }

    /** 插件启动时执行 */
    default void onEnable() {}

    /** 插件关闭时执行 */
    default void onDisable() {}

    /** 插件加载时执行 */
    default void onLoad() {}

    default void onUnload() {}

    PluginProperty getProperty();

    Logger getLogger();

    void setLogger(Logger logger);

    void setProperty(PluginProperty property);

    File getDataFolder();

    void setDataFolder(File folder);

    default File getConfigurationFile() {
        return new File(getDataFolder(), CONFIGURATION_FILE_NAME);
    }

    default boolean copyResource(String path, File to, boolean replace) throws IOException {
        return ResourceUtility.copyResource(getClass().getClassLoader(), path, to, replace);
    }

    default boolean copyDefaultConfiguration(boolean replace) throws IOException {
        return copyResource(CONFIGURATION_FILE_NAME, getConfigurationFile(), replace);
    }

    default <T extends Preservable<File>> T loadConfigurationAs(Class<T> clazz) throws IOException {
        return loadFileAs(clazz, getConfigurationFile());
    }

    default <T extends Preservable<File>> T loadFileAs(Class<T> clazz, File file) throws IOException {
        return getXiaomingBot().getFileLoader().load(clazz, file);
    }

    default <T extends Preservable<File>> T loadFileAs(Class<T> clazz, String fileName) throws IOException {
        return loadFileAs(clazz, new File(getDataFolder(), fileName));
    }

    default <T extends Preservable<File>> T loadConfigurationOrSupply(Class<T> clazz, Supplier<T> supplier) {
        return loadFileOrSupply(clazz, getConfigurationFile(), supplier);
    }

    default <T extends Preservable<File>> T loadFileOrSupply(Class<T> clazz, File file, Supplier<T> supplier) {
        return getXiaomingBot().getFileLoader().loadOrSupply(clazz, file, supplier);
    }

    default <T extends Preservable<File>> T loadFileOrSupply(Class<T> clazz, String fileName, Supplier<T> supplier) {
        return loadFileOrSupply(clazz, new File(getDataFolder(), fileName), supplier);
    }

    default void throwUnsupportedVersionException(String message) {
        throw new UnsupportedVersionException(message +
                "(in plugin: " + getCompleteName() + "," +
                "version: " + getVersion() + ")");
    }

    default void throwUnsupportedVersionException(String message, String requireVersion) {
        throw new UnsupportedVersionException(message +
                "(in plugin: " + getCompleteName() + "," +
                "version: " + getVersion() + ", " +
                "requireVersion: " + requireVersion + ")");
    }
}