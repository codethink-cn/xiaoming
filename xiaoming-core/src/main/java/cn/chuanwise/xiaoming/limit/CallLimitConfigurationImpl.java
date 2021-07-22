package cn.chuanwise.xiaoming.limit;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class CallLimitConfigurationImpl implements CallLimitConfiguration {
    long coolDown = TimeUnit.SECONDS.toMillis(1);
    long period = TimeUnit.HOURS.toMillis(12);
    int top = 20;
    long deltaNoticeTime = period;
}
