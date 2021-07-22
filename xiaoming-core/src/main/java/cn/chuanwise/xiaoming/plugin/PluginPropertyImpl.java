package cn.chuanwise.xiaoming.plugin;

import lombok.*;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chuanwise
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PluginPropertyImpl extends ConcurrentHashMap<String, Object> implements PluginProperty {
    transient File file;
    transient XiaomingPlugin plugin;

    @Override
    public String getName() {
        final String jarFileName = file.getName();
        final Object nameObject = get("name");
        if (nameObject instanceof String) {
            return ((String) nameObject);
        } else {
            return jarFileName.substring(0, jarFileName.lastIndexOf('.'));
        }
    }

    @Override
    public String getVersion() {
        final Object versionObject = get("version");
        if (versionObject instanceof String) {
            return ((String) versionObject).toLowerCase();
        } else {
            return "unknown";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PluginPropertyImpl that = (PluginPropertyImpl) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file);
    }
}
