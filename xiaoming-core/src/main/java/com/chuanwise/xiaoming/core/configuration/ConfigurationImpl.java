package com.chuanwise.xiaoming.core.configuration;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.core.limit.CallLimitConfigImpl;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
public class ConfigurationImpl extends JsonFilePreservable implements Configuration {
    transient XiaomingBot xiaomingBot;

    boolean debug = false;
    boolean enablePreviewFunctions = false;

    CallLimitConfigImpl groupCallConfig = new CallLimitConfigImpl();
    CallLimitConfigImpl privateCallConfig = new CallLimitConfigImpl();

    int maxIterateTime = 20;

    boolean enableLicense = false;

    boolean enableStartLog = false;

    boolean enableClearCall = false;
    Set<String> clearCallPrefixes = new LinkedHashSet<>();
    String clearCallGroupTag = "clear-call";

    long maxUserInputWaitTime = TimeUnit.MINUTES.toMillis(10);
    long maxUserGlobalInputWaitTime = TimeUnit.MINUTES.toMillis(10);
    long maxUserPrivateInputWaitTime = TimeUnit.MINUTES.toMillis(10);
    long maxUserGroupInputWaitTime = TimeUnit.MINUTES.toMillis(10);

    long optimizePeriod = TimeUnit.MINUTES.toMillis(30);
    long savePeriod = TimeUnit.HOURS.toMillis(1);
}
