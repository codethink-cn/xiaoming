package cn.chuanwise.xiaoming.interactor.caughter;

import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

@Data
public class InteractorErrorCaughtHandler<T extends Throwable> {
    final Class<T> throwableClass;
    final InteractorThrowableCaughter<T> caughter;
    final Plugin plugin;
    final boolean shared;
}