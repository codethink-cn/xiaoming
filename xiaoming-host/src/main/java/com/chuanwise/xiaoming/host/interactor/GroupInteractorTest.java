package com.chuanwise.xiaoming.host.interactor;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.annotation.GroupInteractor;
import com.chuanwise.xiaoming.api.annotation.RequirePermission;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl;

@GroupInteractor
public class GroupInteractorTest extends MessageInteractorImpl {
    @Filter("test")
    public void onMessage(XiaomingUser user) {
        user.sendMessage("群聊交互器普通交互方法响应");
    }

    @Filter("test")
    @RequirePermission("test")
    public void onPermissionedMessage(XiaomingUser user) {
        user.sendMessage("需要权限 test 的群聊交互器普通交互方法响应");
    }
}