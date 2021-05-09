package com.chuanwise.xiaoming.api.limit;

import org.jetbrains.annotations.NotNull;

public interface UserCallLimiter extends CallLimiter<Long, UserCallRecord> {
    @Override
    UserCallRecord newRecordInstance();
}
