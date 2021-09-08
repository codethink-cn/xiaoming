package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.utility.CheckUtility;
import cn.chuanwise.utility.MapUtility;
import cn.chuanwise.utility.ReflectUtility;
import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消息处理函数管理器
 */
@Getter
public class EventManagerImpl extends ModuleObjectImpl implements EventManager {
    final Map<ListenerPriority, List<ListenerHandler>> listeners = new ConcurrentHashMap<>();

    public EventManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public Map<ListenerPriority, List<ListenerHandler>> getListeners() {
        return Collections.unmodifiableMap(listeners);
    }

    @Override
    public void unregisterListeners(Plugin plugin) {
        CheckUtility.nonNull(plugin, "plugin");
        listeners.values().forEach(list -> list.removeIf(handler -> Objects.equals(handler.getPlugin(), plugin)));
    }

    @Override
    public <T extends Plugin> void registerListeners(Listeners<T> listeners, T plugin) {
        if (listeners instanceof PluginObject) {
            final PluginObject<T> pluginObject = (PluginObject<T>) listeners;
            pluginObject.setXiaomingBot(getXiaomingBot());
            pluginObject.setPlugin(plugin);
        }
        listeners.onRegister();
        ReflectUtility.forEachDeclaredMethod(listeners.getClass(), (c, method) -> {
            final EventListener[] handlers = method.getAnnotationsByType(EventListener.class);
            final Parameter[] parameters = method.getParameters();
            if (handlers.length == 0 || parameters.length != 1) {
                return;
            }

            final EventListener handler = handlers[0];
            final Parameter parameter = parameters[0];

            // 如果监听函数的参数不是 Event 则免谈
            final Class<?> eventClass = parameter.getType();
            if (!Event.class.isAssignableFrom(eventClass)) {
                getLogger().error("监听函数 " + method.getName() + " 虽带有监听器注解，但参数并非事件，注册失败");
                return;
            }

            final List<ListenerHandler> listenerHandlers = MapUtility.getOrPutSupply(this.listeners, handler.priority(), CopyOnWriteArrayList::new);
            listenerHandlers.add(new ListenerHandler(handler.priority(), eventClass, event -> {
                try {
                    method.setAccessible(true);
                    method.invoke(listeners, event);
                } catch (IllegalAccessException ignored) {
                } catch (InvocationTargetException exception) {
                    getLogger().error("监听函数 " + method.getName() + " 响应事件 " + event + " 时出现异常", exception.getCause());
                }
            }, handler.ignoreCancelled(), plugin));
        });
    }

    @Override
    public void registerListener(ListenerHandler<?> handler) {
        final Plugin plugin = handler.getPlugin();
        CheckUtility.checkState(getXiaomingBot().getStatus() == XiaomingBot.Status.ENABLING || Objects.nonNull(plugin),
                "can not register listener as xiaoming core");
        final List<ListenerHandler> samePriorityListeners = MapUtility.getOrPutSupply(listeners, handler.getPriority(), CopyOnWriteArrayList::new);
        samePriorityListeners.add(handler);
    }
}