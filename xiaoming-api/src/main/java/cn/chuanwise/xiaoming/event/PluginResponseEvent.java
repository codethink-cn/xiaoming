package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.plugin.XiaomingPlugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PluginResponseEvent extends XiaomingEvent {
    XiaomingPlugin xiaomingPlugin;
    XiaomingUser user;
}
