package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PluginResponseEvent extends XiaomingEvent {
    XiaomingPlugin xiaomingPlugin;
}
