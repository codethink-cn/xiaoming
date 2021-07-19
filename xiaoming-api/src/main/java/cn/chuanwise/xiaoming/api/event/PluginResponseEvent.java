package cn.chuanwise.xiaoming.api.event;

import cn.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PluginResponseEvent extends XiaomingEvent {
    XiaomingPlugin xiaomingPlugin;
    XiaomingUser user;
}
