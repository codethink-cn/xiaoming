package com.chuanwise.xiaoming.api.configuration;

import com.chuanwise.xiaoming.api.limit.CallLimitConfig;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;
import java.util.Set;

/**
 * 小明配置文件数据
 */
public interface Configuration extends Preservable<File>, XiaomingObject {
    boolean isDebug();

    boolean isEnablePreviewFunctions();

    void setEnablePreviewFunctions(boolean enablePreviewFunction);

    boolean isEnableStartLog();

    void setEnableStartLog(boolean enableStartLog);

    void setMaxIterateTime(int maxIterateTime);

    int getMaxIterateTime();

    void setDebug(boolean debug);

    long getMaxUserInputWaitTime();

    void setMaxUserInputWaitTime(long time);

    long getMaxUserGlobalInputWaitTime();

    void setMaxUserGlobalInputWaitTime(long time);

    long getMaxUserPrivateInputWaitTime();

    void setMaxUserPrivateInputWaitTime(long time);

    long getMaxUserGroupInputWaitTime();

    void setMaxUserGroupInputWaitTime(long time);

    CallLimitConfig getGroupCallConfig();

    CallLimitConfig getPrivateCallConfig();

    long getSavePeriod();

    void setSavePeriod(long savePeriod);

    long getOptimizePeriod();

    void setOptimizePeriod(long optimizePeriod);

    boolean isEnableLicense();

    void setEnableLicense(boolean enableLicense);

    boolean isEnableClearCall();

    void setEnableClearCall(boolean enableClearCall);

    Set<String> getClearCallPrefixes();

    void setClearCallPrefixes(Set<String> clearCallPrefixes);

    String getClearCallGroupTag();

    void setClearCallGroupTag(String tag);
}
