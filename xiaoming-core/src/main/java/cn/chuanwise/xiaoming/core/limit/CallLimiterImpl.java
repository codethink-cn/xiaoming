package cn.chuanwise.xiaoming.core.limit;

import cn.chuanwise.xiaoming.api.limit.CallLimitConfig;
import cn.chuanwise.xiaoming.api.limit.CallLimiter;
import cn.chuanwise.xiaoming.api.limit.CallRecord;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chuanwise
 */
public abstract class CallLimiterImpl<Key, Value extends CallRecord> implements CallLimiter<Key, Value> {
    private transient CallLimitConfigImpl config = null;

    private final Map<Key, Value> records = new HashMap<>();

    public CallLimiterImpl() {
    }

    public CallLimiterImpl(CallLimitConfig config) {
        setConfig(config);
    }

    @Override
    public Map<Key, Value> getRecords() {
        return records;
    }

    @Override
    public CallLimitConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(CallLimitConfig config) {
        this.config = (CallLimitConfigImpl) config;
    }

    @Override
    public Value getCallRecords(@NotNull final Key key) {
        return records.get(key);
    }

    @Override
    public Value getOrPutCallRecords(@NotNull final Key key) {
        Value groupUserCallRecord = getCallRecords(key);
        if (Objects.isNull(groupUserCallRecord)) {
            groupUserCallRecord = newRecordInstance();
            records.put(key, groupUserCallRecord);
        }
        return groupUserCallRecord;
    }

    @Override
    public boolean isTooFastSoUncallable(@NotNull final Key key) {
        final Value userCallRecord = getCallRecords(key);
        if (Objects.isNull(userCallRecord)) {
            return false;
        } else {
            return config.getCoolDown() > 0 && userCallRecord.isTooFastSoUncallable(config);
        }
    }

    @Override
    public boolean isTooManySoUncallable(@NotNull final Key key) {
        final Value userCallRecord = getCallRecords(key);
        if (Objects.isNull(userCallRecord)) {
            return false;
        } else {
            return config.getTop() > 0 && userCallRecord.isTooManySoUncallable(config);
        }
    }

    @Override
    public void addCallRecord(@NotNull final Key key) {
        getOrPutCallRecords(key).addNewCall(config);
    }

    @Override
    public boolean shouldNotice(@NotNull Key key) {
        final Value records = getCallRecords(key);
        return Objects.nonNull(records) && records.getLastNoticeTime() + config.getDeltaNoticeTime() < System.currentTimeMillis();
    }

    @Override
    public void setNoticed(@NotNull Key key) {
        getOrPutCallRecords(key).updateLastNoticeTime();
    }
}