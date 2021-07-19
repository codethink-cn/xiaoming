package cn.chuanwise.xiaoming.api.limit;

import java.util.Map;

public interface CallLimiter<Key, Record extends CallRecord> {
    Map<Key, Record> getRecords();

    CallLimitConfig getConfig();

    void setConfig(CallLimitConfig config);

    Record getCallRecords(Key key);

    Record getOrPutCallRecords(Key key);

    Record newRecordInstance();

    default boolean uncallable(Key key) {
        return isTooFastSoUncallable(key) || isTooManySoUncallable(key);
    }

    boolean isTooFastSoUncallable(Key key);

    boolean isTooManySoUncallable(Key key);

    void addCallRecord(Key key);

    boolean shouldNotice(Key key);

    void setNoticed(Key key);
}
