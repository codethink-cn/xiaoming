package cn.codethink.xiaoming.event;

import cn.chuanwise.common.api.ExceptionConsumer;
import cn.codethink.common.util.*;
import cn.codethink.xiaoming.AbstractBot;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.annotation.EventHandler;
import cn.codethink.xiaoming.concurrent.BotPromise;
import cn.codethink.xiaoming.logger.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * @see cn.codethink.xiaoming.event.EventManager
 * @author Chuanwise
 */
public class EventManagerImpl
        extends AbstractBotObject
        implements EventManager {
    
    private final Logger logger;
    
    private final Map<Priority, List<cn.codethink.xiaoming.event.EventHandler>> listeners = new ConcurrentHashMap<>();
    
    public EventManagerImpl(AbstractBot bot) {
        super(bot);
        
        logger = bot.getBotConfiguration().getLoggerFactory().getLogger("event manager");
    }
    
    @Override
    public BotPromise<Boolean> broadcastEvent(Event event) {
        Preconditions.nonNull(event, "event");
    
        return ((AbstractBot) bot).getScheduler().submit(() -> broadcastEventSync(event));
    }
    
    @Override
    public boolean broadcastEventSync(Event event) {
        Preconditions.nonNull(event, "event");
    
        final boolean highest = broadcastEvent(event, Priority.HIGHEST);
        final boolean high = broadcastEvent(event, Priority.HIGH);
        final boolean normal = broadcastEvent(event, Priority.NORMAL);
        final boolean low = broadcastEvent(event, Priority.LOW);
        final boolean lowest = broadcastEvent(event, Priority.LOWEST);
    
        return highest || high || normal || low || lowest;
    }
    
    private boolean broadcastEvent(Event event, Priority priority) {
        final List<cn.codethink.xiaoming.event.EventHandler> eventHandlers = this.listeners.get(priority);
        
        boolean handled = false;
        if (Collections.nonEmpty(eventHandlers)) {
            for (cn.codethink.xiaoming.event.EventHandler eventHandler : eventHandlers) {
                
                // interception means stop listening immediately
                if (event.isIntercepted()) {
                    return handled;
                }
                
                try {
                    eventHandler.handleEvent(event);
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
        Preconditions.nonNull(object, "object");
    
        final Method[] methods = object.getClass().getDeclaredMethods();
    
        for (Method method : methods) {
            // 扫描所有带有 EventHandler 注解的方法
            // 将它们注册为监听器
            final EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
    
            // 生成监听器
            final cn.codethink.xiaoming.event.EventHandler eventHandler;
            if (Modifiers.isStatic(method)) {
                eventHandler = MethodEventHandler.ofStaticMethod(method, annotation.priority(), annotation.alwaysValid());
            } else {
                eventHandler = MethodEventHandler.ofMethod(object, method, annotation.priority(), annotation.alwaysValid());
            }
    
            // 添加到监听器列表中
            final List<cn.codethink.xiaoming.event.EventHandler> samePriorityEventHandlers = Maps.getOrPutGet(listeners, eventHandler.getPriority(), CopyOnWriteArrayList::new);
            samePriorityEventHandlers.add(eventHandler);
        }
    }
    
    @Override
    public <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action, Priority priority, boolean alwaysValid) {
        Preconditions.nonNull(eventClass, "event class");
        Preconditions.nonNull(action, "action");
        Preconditions.nonNull(priority, "priority");
    
        final List<cn.codethink.xiaoming.event.EventHandler> samePriorityEventHandlers = Maps.getOrPutGet(listeners, priority, CopyOnWriteArrayList::new);
        samePriorityEventHandlers.add(new EventHandlerImpl<>(eventClass, priority, alwaysValid, action));
    }
    
    @Override
    public boolean unregisterEvents(Class<?> eventClass) {
        Preconditions.nonNull(eventClass, "event class");
    
        return unregisterAllListeners(x -> eventClass.isAssignableFrom(x.getEventClass()));
    }
    
    @Override
    public boolean unregisterListeners(Object listeners) {
        Preconditions.nonNull(listeners, "listeners");
    
        return unregisterAllListeners(x -> x instanceof MethodEventHandler && Objects.equals(((MethodEventHandler) x).source, listeners));
    }
    
    @Override
    public boolean unregisterListeners(Class<?> listenersClass) {
        Preconditions.nonNull(listenersClass, "listeners class");
    
        return unregisterAllListeners(x -> x instanceof MethodEventHandler && listenersClass.isInstance(((MethodEventHandler) x).source));
    }
    
    private boolean unregisterListeners(Priority priority, Predicate<cn.codethink.xiaoming.event.EventHandler> filter) {
        final List<cn.codethink.xiaoming.event.EventHandler> eventHandlers = this.listeners.get(priority);
        
        boolean removed = false;
        if (Collections.nonEmpty(eventHandlers)) {
            removed = eventHandlers.removeIf(filter);
            if (eventHandlers.isEmpty()) {
                this.listeners.remove(priority);
            }
        }
        
        return removed;
    }
    
    private boolean unregisterAllListeners(Predicate<cn.codethink.xiaoming.event.EventHandler> filter) {
    
        final boolean highest = unregisterListeners(Priority.HIGHEST, filter);
        final boolean high = unregisterListeners(Priority.HIGH, filter);
        final boolean normal = unregisterListeners(Priority.NORMAL, filter);
        final boolean low = unregisterListeners(Priority.LOW, filter);
        final boolean lowest = unregisterListeners(Priority.LOWEST, filter);
        
        return highest || high || normal || low || lowest;
    }
    
    @Override
    public void unregisterAllListeners() {
        listeners.clear();
    }
}
