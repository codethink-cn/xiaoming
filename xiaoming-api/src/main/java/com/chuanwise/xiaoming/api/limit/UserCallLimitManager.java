package com.chuanwise.xiaoming.api.limit;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;

public interface UserCallLimitManager extends Preservable<File>, XiaomingObject {
    UserCallLimiter getGroupCallLimiter();

    UserCallLimiter getPrivateCallLimiter();
}
