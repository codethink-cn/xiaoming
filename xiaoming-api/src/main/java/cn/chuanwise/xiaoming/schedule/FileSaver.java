package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.object.ModuleObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

/***
 * 小明的文件保存器，独立于 {@link Scheduler}。避免高频 IO 而设置的。
 * 每隔 {@link Configuration#getSavePeriod()} 执行一次 {@link FileSaver#save()}，关闭小明前也会自动保存文件。
 * 将文件加入保存计划中，需要执行 {@link FileSaver#readyToSave(Preservable)}，它将在下一轮保存时保存。
 *
 * @author Chuanwise
 * @date 2021.7.20
 * @since 3.0
 */
public interface FileSaver extends ModuleObject {
    default String getEncode() {
        return getEncodeCharset().name();
    }

    default void setEncode(String encode) {
        setEncodeCharset(Charset.forName(encode));
    }

    Charset getEncodeCharset();

    void setEncodeCharset(Charset encodeCharset);

    /** 立刻执行一次保存 */
    void save();

    /**
     * 将文件加入保存计划，或直接保存。取决于 {@link Configuration#isSaveFileDirectly()}
     * @param preservable 文件
     */
    default void readyToSave(Preservable preservable) {
        final File file = preservable.getFile();
        ConditionUtil.checkArgument(Objects.nonNull(file), "medium can not be null!");

        // 是否直接保存文件
        if (getXiaomingBot().getConfiguration().isSaveFileDirectly()) {
            if (saveOrFail(preservable)) {
                // getLog().info("成功保存文件：" + file.getAbsolutePath());
            } else {
                getLogger().error("保存文件失败：" + file.getAbsolutePath());
                getPreservables().put(file, preservable);
            }
        } else {
            planToSave(preservable);
        }
    }

    /**
     * 将文件加入保存计划
     * @param preservable 文件
     */
    default void planToSave(Preservable preservable) {
        final File file = preservable.getFile();
        ConditionUtil.checkArgument(Objects.nonNull(file), "medium can not be null!");

        getPreservables().put(file, preservable);
    }

    default void save(Preservable preservable) throws IOException {
        final Serializer serializer = preservable.getSerializer();
        serializer.serialize(preservable, preservable.getFile(), getEncode());
    }

    default boolean saveOrFail(Preservable preservable) {
        try {
            save(preservable);
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /** 上一次例行保存的时间，不一定真正保存了东西 */
    long getLastSaveTime();

    /** 上一次真正保存时间，至少保存了一个文件 */
    long getLastValidSaveTime();

    /** 上一次保存至今的保存计划，将在关闭前保存 */
    Map<File, Preservable> getPreservables();
}
