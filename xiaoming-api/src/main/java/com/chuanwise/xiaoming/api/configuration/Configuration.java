package com.chuanwise.xiaoming.api.configuration;

import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;

/**
 * 小明配置文件数据
 */
public interface Configuration extends Preservable<File>, XiaomingObject {
    boolean isDebug();

    void setDebug(boolean debug);

    CallLimitConfig getGroupCallConfig();

    CallLimitConfig getPrivateCallConfig();

    long getAutoSaveDeltaTime();

    void setAutoSaveDeltaTime(long autoSaveDeltaTime);

    int getMaxMainThreadNumber();

    boolean isEnableLicense();

    void enableLicence();

    void disableLicence();

    String getLicenseName();

    void setMaxReceptThreadNumber(int maxReceptThreadNumber);

    int getMaxReceptThreadNumber();
}
