package cn.chuanwise.xiaoming.api.plugin;

import cn.chuanwise.exception.UnsupportedOperationVersion;
import cn.chuanwise.xiaoming.api.language.Language;
import cn.chuanwise.xiaoming.api.object.XiaomingObject;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.api.utility.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * 小明插件主类
 * 插件主类是小明加载插件的唯一入口。通过 {@code XiaomingClassLoader} 加载插件类后，会检查该类是否是
 * 本接口的实现。如果是，才会继续通过多态性调用本接口的 {@code onLoad()} 和 {@code onEnable()} 以
 * 启动插件。
 * {@code onLoad()} 的执行顺序是随机的，但 {@code onEnable()} 的执行顺序必然是一个前置插件关系的拓
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

    /** 获得插件语言包 */
    Language getLanguage();

    /**
     * 设置插件语言包。此方法不要轻易调用，建议使用 {@code loadLanguageAs(...)}
     * @param language 语言包信息
     */
    void setLanguage(Language language);

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

    Logger getLog();

    void setLog(Logger log);

    void setProperty(PluginProperty property);

    File getDataFolder();

    void setDataFolder(File folder);

    default File getConfigurationFile() {
        return new File(getDataFolder(), CONFIGURATION_FILE_NAME);
    }

    default boolean copyResourceTo(String path, File to) throws IOException {
        if (!to.isFile()) {
            to.createNewFile();
        }
        return FileUtils.copyResource(getClass().getClassLoader(), path, to);
    }

    default boolean copyDefaultConfiguration() throws IOException {
        return copyResourceTo(CONFIGURATION_FILE_NAME, getConfigurationFile());
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

    default <T extends Preservable<File>> T loadConfigurationOrSupplie(Class<T> clazz, Supplier<T> supplier) {
        return loadFileOrSupplie(clazz, getConfigurationFile(), supplier);
    }

    default <T extends Preservable<File>> T loadFileOrSupplie(Class<T> clazz, File file, Supplier<T> supplier) {
        return getXiaomingBot().getFileLoader().loadOrSupplie(clazz, file, supplier);
    }

    default <T extends Preservable<File>> T loadFileOrSupplie(Class<T> clazz, String fileName, Supplier<T> supplier) {
        return loadFileOrSupplie(clazz, new File(getDataFolder(), fileName), supplier);
    }

    default void throwUnsupportedOperationVersion(String message) {
        throw new UnsupportedOperationVersion(message +
                "(in plugin: " + getCompleteName() + "," +
                "version: " + getVersion() + ")");
    }

    default void throwUnsupportedOperationVersion(String message, String requireVersion) {
        throw new UnsupportedOperationVersion(message +
                "(in plugin: " + getCompleteName() + "," +
                "version: " + getVersion() + ", " +
                "requireVersion: " + requireVersion + ")");
    }
}