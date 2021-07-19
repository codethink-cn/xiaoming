package cn.chuanwise.xiaoming.api.limit;

import cn.chuanwise.xiaoming.api.object.XiaomingObject;

public interface UserCallLimitManager extends XiaomingObject {
    UserCallLimiter getGroupCallLimiter();

    UserCallLimiter getPrivateCallLimiter();
}
