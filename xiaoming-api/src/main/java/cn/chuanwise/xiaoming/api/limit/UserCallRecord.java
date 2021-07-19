package cn.chuanwise.xiaoming.api.limit;

import cn.chuanwise.xiaoming.api.record.SizedRecorder;

public interface UserCallRecord extends CallRecord {
    SizedRecorder<Long> getRecentCalls();
}
