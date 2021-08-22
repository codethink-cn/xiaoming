package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.toolkit.serialize.serializer.configuration.SerializerConfiguration;
import cn.chuanwise.xiaoming.limit.CallLimitConfiguration;
import cn.chuanwise.toolkit.preservable.file.FilePreservable;
import cn.chuanwise.xiaoming.object.XiaomingObject;

import java.util.Set;

/**
 * 小明配置文件数据
 */
public interface Configuration extends FilePreservable, XiaomingObject {
    void setStorageDelType(DelType delType);

    DelType getStorageDelType();

    String getStorageEncoding();

    void setStorageEncoding(String storageEncoding);

    String getStorageDecoding();

    void setStorageDecoding(String storageEncoding);

    boolean isDebug();

    boolean isEnablePreviewFunctions();

    void setEnablePreviewFunctions(boolean enablePreviewFunction);

    boolean isEnableStartLog();

    void setEnableStartLog(boolean enableStartLog);

    void setMaxIterateTime(int maxIterateTime);

    int getMaxIterateTime();

    int getMaxRecentMessageBufferSize();

    void setMaxRecentMessageBufferSize(int maxRecentMessageBufferSize);

    int getMaxRecentGroupMessageBufferQuantity();

    void setMaxRecentGroupMessageBufferQuantity(int maxRecentGroupMessageBufferQuantity);

    int getMaxLoadedAccountQuantity();

    void setMaxLoadedAccountQuantity(int maxLoadedAccountQuantity);

    int getMaxRecentGroupMemberMessageBufferQuantity();

    void setMaxRecentGroupMemberMessageBufferQuantity(int maxRecentGroupMemberMessageBufferQuantity);

    int getMaxRecentPrivateMessageBufferQuantity();

    void setMaxRecentPrivateMessageBufferQuantity(int maxRecentPrivateMessageBufferQuantity);

    int getMaxGroupUserQuantityInReceptionist();

    void setMaxGroupUserQuantityInReceptionist(int maxGroupUserQuantityInReceptionist);

    int getMaxMemberUserQuantityInReceptionist();

    void setMaxMemberUserQuantityInReceptionist(int maxMemberUserQuantityInReceptionist);

    int getMaxUserPropertyQuantity();

    void setMaxUserPropertyQuantity(int maxUserPropertyQuantity);

    int getMaxRecentPrivateMessageBufferSize();

    void setMaxRecentPrivateMessageBufferSize(int maxRecentPrivateMessageBufferSize);

    int getMaxReceptionistQuantity();

    void setMaxReceptionistQuantity(int MaxReceptionistQuantity);

    void setDebug(boolean debug);

    long getMaxUserInputWaitTime();

    void setMaxUserInputWaitTime(long time);

    long getMaxUserGlobalInputWaitTime();

    void setMaxUserGlobalInputWaitTime(long time);

    long getMaxUserPrivateInputWaitTime();

    void setMaxUserPrivateInputWaitTime(long time);

    long getMaxUserGroupInputWaitTime();

    void setMaxUserGroupInputWaitTime(long time);

    String getQuietModeGroupTag();

    void setQuietModeGroupTag(String quietModeGroupTag);

    String getEnableGroupTag();

    void setEnableGroupTag(String enableGroupTag);

    String getQuietModeBypassPermission();

    void setQuietModeBypassPermission(String quietModeBypassPermission);

    String getBlockPluginTagPrefix();

    void setBlockPluginTagPrefix(String blockPluginTagPrefix);

    CallLimitConfiguration getGroupCallConfig();

    CallLimitConfiguration getPrivateCallConfig();

    int getMaxVerifyCodeLength();

    void setMaxVerifyCodeLength(int maxVerifyCodeLength);

    Set<String> getVerifyCodeCharacters();

    void setVerifyCodeCharacters(Set<String> verifyCodeCharacters);

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

    boolean isSaveFileDirectly();

    void setSaveFileDirectly(boolean saveFileDirectly);

    boolean isAutoAcceptFriendAddRequest();

    void setAutoAcceptFriendAddRequest(boolean autoAcceptFriendAddRequest);

    boolean isAutoAcceptGroupInvite();

    void setAutoAcceptGroupInvite(boolean autoAcceptGroupInvite);

    int getMaxMainThreadPoolSize();

    void setMaxMainThreadPoolSize(int maxMainThreadPoolSize);

    int getMaxReceptionThreadPoolSize();

    void setMaxReceptionThreadPoolSize(int maxReceptionThreadPoolSize);

    SerializerConfiguration getSerializerConfiguration();

    void setSerializerConfiguration(SerializerConfiguration serializerConfiguration);
}
