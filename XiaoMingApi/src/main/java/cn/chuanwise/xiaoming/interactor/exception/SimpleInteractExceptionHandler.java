package cn.chuanwise.xiaoming.interactor.exception;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

@Data
public class SimpleInteractExceptionHandler<T extends Throwable> implements InteractExceptionHandler<T> {
    final Class<T> handledClass;
    final InteractExceptionHandler<T> handler;
    final Plugin plugin;
    final boolean shared;

    @Override
    public void handle(InteractorContext context, T t) throws Throwable {
        handler.handle(context, t);
    }
}