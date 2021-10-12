package cn.chuanwise.xiaoming.limit;

import java.util.Map;
import java.util.Objects;

public interface CallLimiter {
    Map<Long, CallRecord> getCallRecords();

    CallLimitConfiguration getConfiguration();

    void setConfiguration(CallLimitConfiguration configuration);

    default CallRecord forCallCallRecords(long code) {
        return getCallRecords().get(code);
    }

    default boolean uncallable(long code) {
        return isTooFastSoUncallable(code) || isTooManySoUncallable(code);
    }

    default boolean isTooFastSoUncallable(long code) {
        final CallRecord callRecord = forCallCallRecords(code);
        return Objects.nonNull(callRecord) && callRecord.isTooFastSoUncallable(getConfiguration());
    }

    default boolean isTooManySoUncallable(long code) {
        final CallRecord callRecord = forCallCallRecords(code);
        return Objects.nonNull(callRecord) && callRecord.isTooManySoUncallable(getConfiguration());
    }

    void addCallRecord(long code);

    default boolean shouldNotice(long code) {
        final CallRecord records = forCallCallRecords(code);
        return Objects.nonNull(records) && records.getLastNoticeTime() + getConfiguration().getDeltaNoticeTime() < System.currentTimeMillis();
    }

    void setNoticed(long code);
}
