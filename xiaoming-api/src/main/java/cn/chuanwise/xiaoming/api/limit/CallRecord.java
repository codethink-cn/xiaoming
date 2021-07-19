package cn.chuanwise.xiaoming.api.limit;

public interface CallRecord {
    long getLastNoticeTime();

    void updateLastNoticeTime();

    long getLastestRecord();

    void addNewCall(CallLimitConfig config);

    long getEarlyestRecord();

    boolean callable(CallLimitConfig config);

    // 因为在一定时间内调用太多次而不能调用
    boolean isTooManySoUncallable(CallLimitConfig config);

    // 因为两次调用之间太快而不能调用
    boolean isTooFastSoUncallable(CallLimitConfig config);

    Long[] list();
}
