package com.chuanwise.xiaoming.core.interactor.message;

import com.chuanwise.xiaoming.api.interactor.message.MessageInteractor;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.core.interactor.InteractorImpl;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

public class MessageInteractorImpl extends InteractorImpl implements MessageInteractor {
    @Getter
    @Setter
    XiaomingPlugin plugin;

    @Override
    public void initialize() {
        final Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            register(method);
        }
    }
}
