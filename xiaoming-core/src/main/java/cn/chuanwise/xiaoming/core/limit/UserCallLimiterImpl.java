package cn.chuanwise.xiaoming.core.limit;

import cn.chuanwise.xiaoming.api.limit.UserCallLimiter;
import cn.chuanwise.xiaoming.api.limit.UserCallRecord;
import org.jetbrains.annotations.NotNull;

public class UserCallLimiterImpl extends CallLimiterImpl<Long, UserCallRecord> implements UserCallLimiter {
    @Override
    @NotNull
    public UserCallRecordImpl newRecordInstance() {
        return new UserCallRecordImpl();
    }
}
