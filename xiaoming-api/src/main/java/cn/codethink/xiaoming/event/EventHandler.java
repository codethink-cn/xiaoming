package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Priority;

/**
 * 事件处理器
 *
 * @author Chuanwise
 */
public interface EventHandler {
    
    /**
     * 处理事件
     *
     * @param event 事件
     * @throws Exception 监听事件时出现异常
     * @return 是否监听了该事件
     */
    boolean handleEvent(Event event) throws Exception;
    
    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    Class<?> getEventClass();
    
    /**
     * 获取监听器优先级
     *
     * @return 监听器优先级
     */
    Priority getPriority();
    
    /**
     * 判断是否在事件取消后仍然触发
     *
     * @return 是否在事件取消后仍然触发
     */
    boolean isAlwaysValid();
}
