package cn.chuanwise.xiaoming.api.event;

import cn.chuanwise.xiaoming.api.object.PluginObject;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.ListenerHost;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * 小明的事件监听器
 */
public interface EventListener extends ListenerHost, PluginObject {
    /**
     * 重新载入消息处理方法
     */
    void initialize();

    boolean onEvent(Event event) throws Exception;

    default Object onParameter(Event event, Parameter parameter) {
        return null;
    }

    Set<Method> getHandlerMethods();
}
