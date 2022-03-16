package cn.codethink.xiaoming.event;

import cn.codethink.api.ExceptionConsumer;
import cn.codethink.util.*;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.annotation.EventHandler;
import cn.codethink.xiaoming.logger.Logger;
import cn.codethink.xiaoming.logger.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @see cn.codethink.xiaoming.event.EventManager
 * @author Chuanwise
 */
public class SimpleEventManager
        extends AbstractBotObject
        implements EventManager {
    
    private final Logger logger = LoggerFactory.of("event manager");
    
    private final Map<Priority, List<Listener>> listeners = new ConcurrentHashMap<>();
    
    public SimpleEventManager(Bot bot) {
        super(bot);
    }
    
    @Override
    public boolean handleEvent(Object event) {
        Preconditions.namedArgumentNonNull(event, "event");
    
        final boolean highest = handleEvent(event, Priority.HIGHEST);
        final boolean high = handleEvent(event, Priority.HIGH);
        final boolean normal = handleEvent(event, Priority.NORMAL);
        final boolean low = handleEvent(event, Priority.LOW);
        final boolean lowest = handleEvent(event, Priority.LOWEST);
    
        return highest || high || normal || low || lowest;
    }
    
    private boolean handleEvent(Object event, Priority priority) {
        final List<Listener> listeners = this.listeners.get(priority);
        
        boolean handled = false;
        if (Collections.nonEmpty(listeners)) {
            for (Listener listener : listeners) {
                try {
                    listener.handleEvent(event);
                    handled = true;
                } catch (Throwable throwable) {
                    logger.error("监听事件时出现异常", throwable);
                }
            }
        }
        
        return handled;
    }
    
    @Override
    public void registerListeners(Object object) {
        Preconditions.namedArgumentNonNull(object, "object");
    
        final Method[] methods = object.getClass().getDeclaredMethods();
    
        for (Method method : methods) {
            // 扫描所有带有 EventHandler 注解的方法
            // 将它们注册为监听器
            final EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
    
            // 生成监听器
            final Listener listener;
            if (Modifiers.isStatic(method)) {
                listener = MethodListener.ofStaticMethod(method, annotation.priority(), annotation.alwaysValid());
            } else {
                listener = MethodListener.ofMethod(object, method, annotation.priority(), annotation.alwaysValid());
            }
    
            // 添加到监听器列表中
            final List<Listener> samePriorityListeners = Maps.getOrPutGet(listeners, listener.getPriority(), CopyOnWriteArrayList::new);
            samePriorityListeners.add(listener);
        }
    }
    
    @Override
    public <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action, Priority priority, boolean alwaysValid) {
        Preconditions.namedArgumentNonNull(eventClass, "event class");
        Preconditions.namedArgumentNonNull(action, "action");
        Preconditions.namedArgumentNonNull(priority, "priority");
    
        final List<Listener> samePriorityListeners = Maps.getOrPutGet(listeners, priority, CopyOnWriteArrayList::new);
        samePriorityListeners.add(new SimpleListener<>(eventClass, priority, alwaysValid, action));
    }
}
