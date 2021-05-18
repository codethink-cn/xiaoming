package com.chuanwise.xiaoming.core.config;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.configuration.Configuration;
import com.chuanwise.xiaoming.api.util.TimeUtil;
import com.chuanwise.xiaoming.core.limit.CallLimitConfigImpl;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigurationImpl extends JsonFilePreservable implements Configuration {
    transient XiaomingBot xiaomingBot;

    boolean debug = false;

    CallLimitConfigImpl groupCallConfig = new CallLimitConfigImpl();
    CallLimitConfigImpl privateCallConfig = new CallLimitConfigImpl();

    long autoSaveDeltaTime = TimeUtil.MINUTE_MINS * 10;
    int maxMainThreadNumber = 50;

    boolean enableLicense = false;
    String licenseName = "license";
    int maxReceptThreadNumber = 10;

    @Override
    public void enableLicence() {
        enableLicense = true;
    }

    @Override
    public void disableLicence() {
        enableLicense = false;
    }
}
