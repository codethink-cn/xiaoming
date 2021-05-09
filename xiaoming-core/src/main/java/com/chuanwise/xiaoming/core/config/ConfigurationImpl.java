package com.chuanwise.xiaoming.core.config;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.config.Configuration;
import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.limit.CallLimitConfigImpl;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigurationImpl extends JsonFilePreservable implements Configuration {
    boolean debug = false;

    CallLimitConfig groupCallConfig = new CallLimitConfigImpl();
    CallLimitConfig privateCallConfig = new CallLimitConfigImpl();

    long autoSaveDeltaTime = TimeUtil.MINUTE_MINS * 10;

    XiaomingBot xiaomingBot;
}
