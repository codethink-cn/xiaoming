package cn.chuanwise.xiaoming.limit;

import cn.chuanwise.util.MapUtil;

import java.util.HashMap;
import java.util.Map;

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
        MapUtil.getOrPutSupply(getCallRecords(), key, CallRecordImpl::new).addNewCall(configuration);
    }

    @Override
    public void setNoticed(long code) {
        MapUtil.getOrPutSupply(getCallRecords(), code, CallRecordImpl::new).updateLastNoticeTime();
    }
}