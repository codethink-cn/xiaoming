package com.chuanwise.xiaoming.api.limit;

import com.chuanwise.xiaoming.api.object.XiaomingObject;

import java.io.File;

public interface UserCallLimitManager extends XiaomingObject {
    UserCallLimiter getGroupCallLimiter();

    UserCallLimiter getPrivateCallLimiter();
}
