package cn.chuanwise.xiaoming.limit;

import cn.chuanwise.xiaoming.object.XiaomingObject;

public interface UserCallLimitManager extends XiaomingObject {
    CallLimiter getGroupCallLimiter();

    CallLimiter getPrivateCallLimiter();
}
