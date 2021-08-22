package cn.chuanwise.xiaoming.interactor.context;

import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InteractorContext {
    final XiaomingUser user;
    final InteractorHandler interactor;
    final Plugin plugin;
    final Message message;
    final Map<String, String> arguments;
    final List<Object> finalArguments;

    public String getArgument(String name) {
        if (MapUtility.nonEmpty(arguments)) {
            return arguments.get(name);
        } else {
            return null;
        }
    }
}
