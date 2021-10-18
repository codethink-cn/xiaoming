package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.object.XiaomingObject;

/**
 * 小明配置文件数据
 */
public interface Configuration extends Preservable, XiaomingObject {
    String getStorageEncoding();

    void setStorageEncoding(String storageEncoding);

    String getStorageDecoding();

    void setStorageDecoding(String storageEncoding);

    boolean isDebug();

    boolean isEnableStartLog();

    void setEnableStartLog(boolean enableStartLog);

    void setMaxIterateTime(int maxIterateTime);

    int getMaxIterateTime();

    int getMaxRecentMessageBufferSize();

    void setMaxRecentMessageBufferSize(int maxRecentMessageBufferSize);

    int getMaxUserAttributeQuantity();

    void setMaxUserAttributeQuantity(int maxUserAttributeQuantity);

    int getMaxReceptionistQuantity();

    void setMaxReceptionistQuantity(int MaxReceptionistQuantity);

    int getMaxGroupUserQuantityInReceptionist();

    void setMaxGroupUserQuantityInReceptionist(int maxGroupUserQuantityInReceptionist);

    int getMaxMemberUserQuantityInReceptionist();

    void setMaxMemberUserQuantityInReceptionist(int maxMemberUserQuantityInReceptionist);

    void setDebug(boolean debug);

    long getMaxUserInputTimeout();

    void setMaxUserInputTimeout(long time);

    long getMaxUserPrivateInputTimeout();

    void setMaxUserPrivateInputTimeout(long time);

    long getMaxUserGroupInputTimeout();

    void setMaxUserGroupInputTimeout(long time);

    long getSavePeriod();

    void setSavePeriod(long savePeriod);

    long getOptimizePeriod();

    void setOptimizePeriod(long optimizePeriod);

    boolean isSaveFileDirectly();

    void setSaveFileDirectly(boolean saveFileDirectly);

    boolean isAutoAcceptFriendAddRequest();

    void setAutoAcceptFriendAddRequest(boolean autoAcceptFriendAddRequest);

    boolean isAutoAcceptGroupInvite();

    void setAutoAcceptGroupInvite(boolean autoAcceptGroupInvite);

    int getMaxMainThreadPoolSize();

    long getSendMessagePeriod();

    void setSendMessagePeriod(long sendMessagePeriod);
}
