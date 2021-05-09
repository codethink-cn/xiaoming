package com.chuanwise.xiaoming.core.limit;

import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.limit.UserCallRecord;
import com.chuanwise.xiaoming.api.record.SizedRecorder;
import com.chuanwise.xiaoming.core.record.SizedRecorderImpl;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class UserCallRecordImpl implements UserCallRecord {
    UserCallSizedRecord recentCalls = new UserCallSizedRecord();
    long lastNoticeTime;

    @Override
    public void updateLastNoticeTime() {
        lastNoticeTime = System.currentTimeMillis();
    }

    @Override
    public long getLastestRecord() {
        return recentCalls.latest();
    }

    @Override
    public void addNewCall(CallLimitConfig config) {
        recentCalls.add(System.currentTimeMillis(), config.getTop());
    }

    @Override
    public long getEarlyestRecord() {
        return recentCalls.earlyest();
    }

    @Override
    public boolean callable(final CallLimitConfig config) {
        return !isTooFastSoUncallable(config) && !isTooManySoUncallable(config);
    }

    // 因为在一定时间内调用太多次而不能调用
    @Override
    public boolean isTooManySoUncallable(final CallLimitConfig config) {
        return recentCalls.size() == config.getTop() && getEarlyestRecord() + config.getPeriod() > System.currentTimeMillis();
    }

    // 因为两次调用之间太快而不能调用
    @Override
    public boolean isTooFastSoUncallable(final CallLimitConfig config) {
        return !recentCalls.empty() && System.currentTimeMillis() < getLastestRecord() + config.getCoolDown();
    }

    @Override
    public Long[] list() {
        return recentCalls.list();
    }

    @Override
    public UserCallSizedRecord getRecentCalls() {
        return recentCalls;
    }
}
