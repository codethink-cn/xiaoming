package cn.chuanwise.xiaoming.limit;

public interface CallRecord {
    long getLastNoticeTime();

    void updateLastNoticeTime();

    long getLastestRecord();

    void addNewCall(CallLimitConfiguration configuration);

    long getEarliestRecord();

    default boolean isCallable(CallLimitConfiguration configuration) {
        return !isTooFastSoUncallable(configuration) && !isTooManySoUncallable(configuration);
    }

    // 因为在一定时间内调用太多次而不能调用
    boolean isTooManySoUncallable(CallLimitConfiguration configuration);

    // 因为两次调用之间太快而不能调用
    boolean isTooFastSoUncallable(CallLimitConfiguration configuration);
}
