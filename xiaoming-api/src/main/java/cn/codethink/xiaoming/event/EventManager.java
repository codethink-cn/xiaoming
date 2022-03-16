package cn.codethink.xiaoming.event;

import cn.codethink.api.ExceptionConsumer;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.Priority;

/**
 * 事件管理器
 *
 * @author Chuanwise
 */
public interface EventManager
        extends BotObject {
    
    /**
     * 监听某个事件
     *
     * @param event 事件
     * @return 是否有事件监听器捕捉该事件
     */
    boolean handleEvent(Object event);
    
    /**
     * 注册一些监听器
     *
     * @param object 监听器类
     */
    void registerListeners(Object object);
    
    /**
     * 注册一个监听器
     *
     * @param eventClass 事件类型
     * @param action 监听行为
     * @param priority 优先级
     * @param alwaysValid 是否该监听器总是生效
     * @param <T> 事件类型
     */
    <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action, Priority priority, boolean alwaysValid);
    
    /**
     * 注册一个默认的监听器
     *
     * @param eventClass 事件类型
     * @param action 监听行为
     * @param priority 优先级
     * @param <T> 事件类型
     */
    default <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action, Priority priority) {
        registerListener(eventClass, action, priority, false);
    }
    
    /**
     * 注册一个默认的监听器
     *
     * @param eventClass 事件类型
     * @param action 监听行为
     * @param <T> 事件类型
     */
    default <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action) {
        registerListener(eventClass, action, Priority.NORMAL, false);
    }
}
