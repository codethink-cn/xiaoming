package com.chuanwise.xiaoming.core.plugin;

import com.chuanwise.xiaoming.api.plugin.PluginProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chuanwise
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginPropertyImpl extends ConcurrentHashMap<String, Object> implements PluginProperty {
    transient File file;

    @Override
    public String getName() {
        final String jarFileName = file.getName();
        final Object nameObject = get("name");
        if (nameObject instanceof String) {
            return ((String) nameObject).toLowerCase();
        } else {
            return jarFileName.substring(jarFileName.lastIndexOf('.') + 1);
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
}
