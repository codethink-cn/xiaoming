package com.chuanwise.xiaoming.core.event;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.event.EventListener;
import com.chuanwise.xiaoming.api.event.EventListenerManager;
import com.chuanwise.xiaoming.api.event.ListenerResponseEvent;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.event.Event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 消息处理函数管理器
 */
@Getter
public class EventListenerManagerImpl extends HostObjectImpl implements EventListenerManager {
    Set<EventListener> coreListeners = new CopyOnWriteArraySet<>();

    Map<XiaomingPlugin, Set<EventListener>> pluginListeners = new ConcurrentHashMap<>();

    Queue<Event> events = new ConcurrentLinkedDeque<>();

    public EventListenerManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void call(Event event) {
        final Class<? extends Event> clazz = event.getClass();
        getLog().info("event occured: " + clazz.getName());
        for (EventListener listener : coreListeners) {
            try {
                if (listener.onEvent(event)) {
                    callLater(new ListenerResponseEvent(listener, event));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void callLater(Event event) {
        events.add(event);
        synchronized (events) {
            events.notifyAll();
        }
    }

    @Override
    public void register(EventListener listener, XiaomingPlugin plugin) {
        if (Objects.nonNull(plugin)) {
            getOrPutPluginListeners(plugin).add(listener);
        } else {
            coreListeners.add(listener);
        }
        listener.reloadHandlerMethods(getLog());
        listener.setXiaomingBot(getXiaomingBot());
    }

    @Override
    public Set<EventListener> getPluginListeners(XiaomingPlugin plugin) {
        return pluginListeners.get(plugin);
    }

    @Override
    public Set<EventListener> getOrPutPluginListeners(XiaomingPlugin plugin) {
        Set<EventListener> listeners = getPluginListeners(plugin);
        if (Objects.isNull(listeners)) {
            listeners = new CopyOnWriteArraySet<>();
            pluginListeners.put(plugin, listeners);
        }
        return listeners;
    }

    @Override
    public void run() {
        while (!getXiaomingBot().isStop()) {
            try {
                synchronized (events) {
                    events.wait();
                }
            } catch (InterruptedException ignored) {
            }
            if (getXiaomingBot().isStop()) {
                return;
            }
            for (Event event : events) {
                if (!event.isIntercepted()) {
                    call(event);
                }
            }
            events.clear();
        }
    }

    @Override
    public void denyCoreRegister() {
        coreListeners = Collections.unmodifiableSet(coreListeners);
    }
}