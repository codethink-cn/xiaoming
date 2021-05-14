package com.chuanwise.xiaoming.host.interactor;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.GroupInteractor;
import com.chuanwise.xiaoming.api.annotation.PrivateInteractor;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl;

public class InteractorTest extends MessageInteractorImpl {
    @GroupInteractor
    @Filter("test")
    public void onGroup(XiaomingUser user) {
        user.sendMessage("普通上下文交互器群聊交互方法响应");
    }

    @PrivateInteractor
    @Filter("test")
    public void onPrivate(XiaomingUser user) {
        user.sendMessage("普通上下文交互器私聊交互方法响应");
    }

    @GroupInteractor
    @PrivateInteractor
    @Filter("test")
    public void onDouble(XiaomingUser user) {
        user.sendMessage("普通上下文交互器群聊|私聊交互方法响应");
    }

    @Filter("test")
    public void onGlobal(XiaomingUser user) {
        user.sendMessage("普通上下文交互器通用交互方法响应");
    }
}
