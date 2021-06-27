package com.chuanwise.xiaoming.core.event;

import com.chuanwise.xiaoming.api.annotation.EventHandler;
import com.chuanwise.xiaoming.api.event.EventListener;
import com.chuanwise.xiaoming.api.util.CollectionUtils;
import com.chuanwise.xiaoming.core.object.PluginObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.event.Event;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Chuanwise
 */
@Getter
public class EventListenerImpl extends PluginObjectImpl implements EventListener {
    Set<Method> handlerMethods = new CopyOnWriteArraySet<>();

    @Override
    public void reloadHandlerMethods(Logger logger) {
        handlerMethods.clear();
        logger.info("正在载入 {} 类中的消息处理方法", getClass().getName());
        final Class<? extends EventListener> clazz = getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                handlerMethods.add(method);
            }
        }

        if (handlerMethods.isEmpty()) {
            logger.error("没有载入任何消息处理方法");
        } else {
            logger.info("成功载入 " + handlerMethods.size() + " 个消息处理方法");
        }
    }

    @Override
    public boolean onEvent(Event event) throws Exception {
        final Class<? extends Event> eventClass = event.getClass();
        for (Method method : handlerMethods) {
            final Parameter[] parameters = method.getParameters();
            final List<Object> arguments = new ArrayList<>();

            // 填充参数
            for (Parameter parameter : parameters) {
                final Class<?> parameterType = parameter.getType();

                if (parameterType.isAssignableFrom(eventClass)) {
                    arguments.add(event);
                } else {
                    final Object argument = onParameter(event, parameter);
                    if (Objects.isNull(argument)) {
                        break;
                    } else {
                        arguments.add(argument);
                    }
                }
            }

            if (arguments.size() == parameters.length) {
                method.invoke(this, arguments.toArray(new Object[0]));
                return true;
            }
        }
        return false;
    }
}