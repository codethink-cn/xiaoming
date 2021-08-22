package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;

import java.util.Map;

/** 交互器自定义器 */
@FunctionalInterface
public interface Customizer {
    InteractorHandler forName(String interactorName);
}