package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.ListenerHost;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * 小明的事件监听器
 */
public interface EventListener extends ListenerHost, XiaomingObject {
    /**
     * 重新载入消息处理方法
     */
    void reloadHandlerMethods(Logger logger);

    boolean onEvent(Event event) throws Exception;

    default Object onParameter(Event event, Parameter parameter) {
        return null;
    }

    Set<Method> getHandlerMethods();
}
