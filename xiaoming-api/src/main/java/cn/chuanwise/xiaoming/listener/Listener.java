package cn.chuanwise.xiaoming.listener;

import net.mamoe.mirai.event.Event;

/** 小明事件监听器 */
@FunctionalInterface
public interface Listener<T extends Event> {
    void listen(T event);
}
