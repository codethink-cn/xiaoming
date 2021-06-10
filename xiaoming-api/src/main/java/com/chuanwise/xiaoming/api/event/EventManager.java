package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import net.mamoe.mirai.event.Event;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * 监听器管理器
 */
public interface EventManager extends ModuleObject {
    /**
     * 由调用线程立即响应一个事件
     * @param event 目标事件
     */
    default void call(Event event) {
        final Consumer<EventListener> handlerCaller = listener -> {
            try {
                if (listener.onEvent(event)) {
                    callLater(new ListenerResponseEvent(listener, event));
                }
            } catch (Exception exception) {
                getLog().error(exception.getMessage(), exception);
            }
        };

        getCoreListeners().forEach(handlerCaller);
        getPluginListeners().values().forEach(set -> set.forEach(handlerCaller));
    }

    /**
     * 让监听器专门开一个线程响应一个事件
     * @param event 目标事件
     */
    default void callLater(Event event) {
        getXiaomingBot().getScheduler().run(() -> {
            call(event);
        });
    }

    /**
     * 注册监听器
     * @param listener 监听器
     * @param plugin 注册方
     */
    default void register(EventListener listener, XiaomingPlugin plugin) {
        if (Objects.nonNull(plugin)) {
            getOrPutPluginListeners(plugin).add(listener);
            listener.setPlugin(plugin);
        } else {
            getCoreListeners().add(listener);
        }
        listener.reloadHandlerMethods(getLog());
        listener.setXiaomingBot(getXiaomingBot());
    }

    /**
     * 获得某个插件注册的所有监听器
     * @param plugin 目标插件
     * @return 该插件注册的所有监听器的集合。如果该插件从未注册过，返回 {@code null}
     */
    default Set<EventListener> getPluginListeners(XiaomingPlugin plugin) {
        return getPluginListeners().get(plugin);
    }

    /**
     * 获得，或新建一个插件注册的所有监听器。
     * @param plugin 注册的插件
     * @return
     */
    default Set<EventListener> getOrPutPluginListeners(XiaomingPlugin plugin) {
        Set<EventListener> listeners = getPluginListeners(plugin);
        if (Objects.isNull(listeners)) {
            listeners = new CopyOnWriteArraySet<>();
            getPluginListeners().put(plugin, listeners);
        }
        return listeners;
    }

    Set<EventListener> getCoreListeners();

    Map<XiaomingPlugin, Set<EventListener>> getPluginListeners();

    default void remove(XiaomingPlugin plugin) {
        getPluginListeners().remove(plugin);
    }

    void denyCoreRegister();
}
