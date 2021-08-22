package cn.chuanwise.xiaoming.interactor.caughter;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
@FunctionalInterface
public interface InteractorThrowableCaughter<T extends Throwable> {
    void caught(InteractorContext context, T throwable) throws Throwable;
}
