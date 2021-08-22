package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.xiaoming.plugin.Plugin;

/**
 * 小明的上下文相关交互器
 * 指令处理器和上下文相关交互器的父类
 * @author Chuanwise
 */
public interface Interactors<T extends Plugin> {
    default void onRegister() { }
}
