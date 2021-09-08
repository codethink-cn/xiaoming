package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.event.XiaomingEvent;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import net.mamoe.mirai.event.CancellableEvent;
import net.mamoe.mirai.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 监听器管理器
 */
public interface EventManager extends ModuleObject {
    /** 所有的监听器 */
    Map<ListenerPriority, List<ListenerHandler>> getListeners();

    /**
     * 获取某插件注册的所有监听器
     * @param plugin 插件
     * @return 如果 plugin 为 null，返回内核监听器，否则为插件监听器
     */
    default List<ListenerHandler> getListeners(Plugin plugin) {
        final List<ListenerHandler> handlers = new ArrayList<>();
        getListeners().values().forEach(list -> handlers.addAll(CollectionUtility.filter(list, handler -> (handler.getPlugin() == plugin))));
        return handlers;
    }

    /** 指定级别的监听器响应 */
    default boolean callEvent(ListenerPriority priority, Event event) {
        final List<ListenerHandler> listenerHandlers = getListeners().get(priority);
        if (CollectionUtility.isEmpty(listenerHandlers)) {
            return false;
        }

        // 输出调试信息
        if (getXiaomingBot().getConfiguration().isDebug()) {
            getLogger().info("正在以「" + priority + "」优先级响应事件：" + event + "（共有 " + listenerHandlers.size() + " 个监听器）");
        }

        boolean interacted = false;
        final Class<? extends Event> eventClass = event.getClass();
        for (ListenerHandler handler : listenerHandlers) {
            // 如果监听的不是这种事件就跳过
            if (!handler.getEventClass().isAssignableFrom(eventClass)) {
                continue;
            }

            boolean shouldInteract = true;
            // 如果这个事件可以被取消
            // 则只有事件没有被取消，且监听器监听取消了的事件时才执行
            if (event instanceof CancellableEvent) {
                shouldInteract = !((CancellableEvent) event).isCancelled() || handler.isIgnoreCancelled();
            }

            if (!shouldInteract) {
                continue;
            }
            try {
                handler.getListener().listen(event);
                interacted = true;
            } catch (Exception exception) {
                getLogger().error("响应事件 " + event + " 时出现异常", exception);
            }
        }

        return interacted;
    }

    /** 由调用线程立即响应一个事件 */
    default boolean callEvent(Event event) {
        if (getXiaomingBot().isDisabled()) {
            getLogger().error("小明已经关闭，不再响应事件：" + event);
            return false;
        }
        if (event instanceof XiaomingObject) {
            ((XiaomingObject) event).setXiaomingBot(getXiaomingBot());
        }
        if (event instanceof XiaomingEvent) {
            ((XiaomingEvent) event).onCall();
        }

        final boolean highest = callEvent(ListenerPriority.HIGHEST, event);
        final boolean high = callEvent(ListenerPriority.HIGH, event);
        final boolean normal = callEvent(ListenerPriority.NORMAL, event);
        final boolean low = callEvent(ListenerPriority.LOW, event);
        final boolean lowest = callEvent(ListenerPriority.LOWEST, event);

        return highest || high || normal || low || lowest;
    }

    /** 异步响应一个事件 */
    default Future<Boolean> callEventAsync(Event event) {
        if (getXiaomingBot().isEnabled()) {
            return getXiaomingBot().getScheduler().run(() -> callEvent(event));
        } else {
            getLogger().error("小明已经关闭，无法异步响应事件：" + event);
            return null;
        }
    }

    /** 异步响应一个事件 */
    default Future<Boolean> callEventAsync(ListenerPriority priority, Event event) {
        if (getXiaomingBot().isEnabled()) {
            return getXiaomingBot().getScheduler().run(() -> callEvent(priority, event));
        } else {
            getLogger().error("小明已经关闭，无法异步响应事件：" + event);
            return null;
        }
    }

    /**
     * 注册事件监听器
     * @param clazz 事件类型
     * @param listener 监听器类型
     * @param plugin 插件类型
     * @param <T> 事件类型参数
     */
    default <T extends Event> void registerListener(Class<T> clazz, Listener<T> listener, Plugin plugin) {
        registerListener(clazz, ListenerPriority.NORMAL, true, listener, plugin);
    }

    void registerListener(ListenerHandler<?> handler);

    default <T extends Event> void registerListener(Class<T> clazz, ListenerPriority priority, boolean ignoreCancelled, Listener<T> listener, Plugin plugin) {
        registerListener(new ListenerHandler<>(priority, clazz, listener, ignoreCancelled, plugin));
    }

    default <T extends Event> void registerListener(Class<T> clazz, ListenerPriority priority, Listener<T> listener, Plugin plugin) {
        registerListener(clazz, priority, true, listener, plugin);
    }

    /** 监听函数的参数只能是事件、XiaomingBot、注册插件这三种之一 */
    <T extends Plugin> void registerListeners(Listeners<T> listeners, T plugin);

    /** 卸载由某插件注册的所有监听器 */
    void unregisterListeners(Plugin plugin);
}
