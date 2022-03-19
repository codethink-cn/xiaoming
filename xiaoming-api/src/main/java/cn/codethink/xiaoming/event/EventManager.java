package cn.codethink.xiaoming.event;

import cn.codethink.common.api.ExceptionConsumer;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.concurrent.BotFuture;

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
    BotFuture<Boolean> handleEvent(Object event);
    
    /**
     * 注册一些监听器
     *
     * @param object 监听器类
     */
    void registerListeners(Object object);
    
    /**
     * 注册一个监听器
     *
     * @param eventClass  事件类型
     * @param action      监听行为
     * @param priority    优先级
     * @param alwaysValid 是否该监听器总是生效
     * @param <T>         事件类型
     */
    <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action, Priority priority, boolean alwaysValid);
    
    /**
     * 注册一个默认的监听器
     *
     * @param eventClass 事件类型
     * @param action     监听行为
     * @param priority   优先级
     * @param <T>        事件类型
     */
    default <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action, Priority priority) {
        registerListener(eventClass, action, priority, false);
    }
    
    /**
     * 注册一个默认的监听器
     *
     * @param eventClass 事件类型
     * @param action     监听行为
     * @param <T>        事件类型
     */
    default <T> void registerListener(Class<T> eventClass, ExceptionConsumer<T> action) {
        registerListener(eventClass, action, Priority.NORMAL, false);
    }
    
    /**
     * 注销某个事件的所有监听器
     *
     * @param eventClass 事件类
     * @return 是否至少注销了一个监听器
     */
    boolean unregisterEvents(Class<?> eventClass);
    
    /**
     * 注销某个事件监听对象里的所有监听器
     *
     * @param listeners 事件监听对象
     * @return 是否至少注销了一个监听器
     */
    boolean unregisterListeners(Object listeners);
    
    /**
     * 注销注册某个类型的事件监听类
     *
     * @param listenersClass 事件监听类
     * @return 是否至少注销了一个监听器
     */
    boolean unregisterListeners(Class<?> listenersClass);
    
    /**
     * 取消注册所有监听器
     */
    void unregisterAllListeners();
}