package com.chuanwise.xiaoming.core.limit;

import com.chuanwise.xiaoming.api.limit.UserCallLimiter;
import com.chuanwise.xiaoming.api.limit.UserCallRecord;
import org.jetbrains.annotations.NotNull;

public class UserCallLimiterImpl extends CallLimiterImpl<Long, UserCallRecord> implements UserCallLimiter {
    @NotNull
    public UserCallRecord newRecordInstance() {
        return new UserCallRecordImpl();
    }
}
