package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.plugin.Plugin;

/** 事件监听器 */
public interface Listeners<T extends Plugin> {
    default void onRegister() { }
}