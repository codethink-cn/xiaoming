package cn.chuanwise.xiaoming.limit;

import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.utility.MapUtility;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chuanwise
 */
public class CallLimiterImpl implements CallLimiter {
    transient CallLimitConfigurationImpl configuration = null;

    final Map<Long, CallRecord> callRecords = new HashMap<>();

    @Override
    public Map<Long, CallRecord> getCallRecords() {
        return callRecords;
    }

    @Override
    public CallLimitConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(CallLimitConfiguration configuration) {
        this.configuration = (CallLimitConfigurationImpl) configuration;
    }

    @Override
    public void addCallRecord(long key) {
        MapUtility.getOrPutSupply(getCallRecords(), key, CallRecordImpl::new).addNewCall(configuration);
    }

    @Override
    public void setNoticed(long code) {
        MapUtility.getOrPutSupply(getCallRecords(), code, CallRecordImpl::new).updateLastNoticeTime();
    }
}