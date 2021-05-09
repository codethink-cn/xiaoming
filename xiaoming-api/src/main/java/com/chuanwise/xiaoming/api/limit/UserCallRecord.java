package com.chuanwise.xiaoming.api.limit;

import com.chuanwise.xiaoming.api.record.SizedRecorder;

public interface UserCallRecord extends CallRecord {
    SizedRecorder<Long> getRecentCalls();
}
