package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.xiaoming.interactor.handler.Interactor;

/** 交互器自定义器 */
@FunctionalInterface
public interface Customizer {
    Interactor forName(String interactorName);
}