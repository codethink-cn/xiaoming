package cn.chuanwise.xiaoming.interactor.exception;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
@FunctionalInterface
public interface InteractExceptionHandler<T extends Throwable> {
    void handle(InteractorContext context, T t) throws Throwable;
}