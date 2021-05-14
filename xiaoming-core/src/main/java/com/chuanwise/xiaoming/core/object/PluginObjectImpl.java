package com.chuanwise.xiaoming.core.object;

import com.chuanwise.xiaoming.api.object.PluginObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import lombok.Data;

@Data
public class PluginObjectImpl extends XiaomingObjectImpl implements PluginObject {
    XiaomingPlugin plugin;
}
