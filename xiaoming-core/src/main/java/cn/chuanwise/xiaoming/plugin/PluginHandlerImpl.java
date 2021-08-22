package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.toolkit.map.PathMapOperator;
import cn.chuanwise.toolkit.serialize.object.DeserializedObjectImpl;
import lombok.*;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chuanwise
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PluginHandlerImpl extends PathMapOperator implements PluginHandler {
    transient File file;
    transient Plugin plugin;

    public PluginHandlerImpl(Map<String, Object> values) {
        super(values);
        getPathGetterConfiguration().setReturnNullIfFail(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PluginHandlerImpl that = (PluginHandlerImpl) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file);
    }
}
