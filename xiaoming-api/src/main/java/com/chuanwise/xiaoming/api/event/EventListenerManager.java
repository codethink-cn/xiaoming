package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.ListenerHost;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 监听器管理器
 */
public interface EventListenerManager extends Runnable {
    /**
     * 由调用线程立即响应一个事件（不推荐使用）
     * @param event 目标事件
     */
    void call(Event event);

    /**
     * 让监听器线程稍后响应一个事件
     * @param event 目标事件
     */
    void callLater(Event event);

    /**
     * 注册监听器
     * @param listener 监听器
     * @param plugin 注册方
     */
    void register(EventListener listener, XiaomingPlugin plugin);

    /**
     * 获得某个插件注册的所有监听器
     * @param plugin 目标插件
     * @return 该插件注册的所有监听器的集合。如果该插件从未注册过，返回 {@code null}
     */
    Set<EventListener> getPluginListeners(XiaomingPlugin plugin);

    /**
     * 获得，或新建一个插件注册的所有监听器。
     * @param plugin 注册的插件
     * @return
     */
    Set<EventListener> getOrPutPluginListeners(XiaomingPlugin plugin);

    Set<EventListener> getCoreListeners();

    Map<XiaomingPlugin, Set<EventListener>> getPluginListeners();

    Queue<Event> getEvents();

    void denyCoreRegister();
}
