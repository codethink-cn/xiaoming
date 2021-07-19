package cn.chuanwise.xiaoming.api.limit;

public interface UserCallLimiter extends CallLimiter<Long, UserCallRecord> {
    @Override
    UserCallRecord newRecordInstance();
}
