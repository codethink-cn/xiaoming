package com.chuanwise.xiaoming.core.limit;

import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class CallLimitConfigImpl implements CallLimitConfig {
    long coolDown = TimeUnit.SECONDS.toMillis(10);
    long period = TimeUnit.HOURS.toMillis(12);
    int top = 10;
    long deltaNoticeTime = period;
}
