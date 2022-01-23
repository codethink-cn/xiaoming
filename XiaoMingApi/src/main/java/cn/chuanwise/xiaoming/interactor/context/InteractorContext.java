package cn.chuanwise.xiaoming.interactor.context;

import cn.chuanwise.util.MapUtil;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InteractorContext {
    final XiaomingUser user;
    final Interactor interactor;
    final Plugin plugin;
    final Message message;
    final Map<String, String> arguments;
    final Map<String, Object> argumentValues;
    final List<Object> finalArguments;

    public String getArgument(String name) {
        if (MapUtil.nonEmpty(arguments)) {
            return arguments.get(name);
        } else {
            return null;
        }
    }
}
