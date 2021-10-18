package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
public class ConfigurationImpl extends AbstractPreservable implements Configuration {
    transient XiaomingBot xiaomingBot;

    boolean debug = false;

    int maxIterateTime = 20;

    /** 存储数据所用编码类型 */
    String storageEncoding = "UTF-8";
    String storageDecoding = "UTF-8";

    /** 自动同意添加好友、加群申请 */
    boolean autoAcceptFriendAddRequest = true;
    boolean autoAcceptGroupInvite = false;

    /** 主线程池最大容量 */
    int maxMainThreadPoolSize = 10;

    /** 启动时在日志群发消息 */
    boolean enableStartLog = false;
    boolean saveFileDirectly = true;

    /** 和用户 */
    long maxUserInputTimeout = TimeUnit.MINUTES.toMillis(10);
    long maxUserPrivateInputTimeout = TimeUnit.MINUTES.toMillis(10);
    long maxUserGroupInputTimeout = TimeUnit.MINUTES.toMillis(10);

    long optimizePeriod = TimeUnit.MINUTES.toMillis(10);
    long savePeriod = TimeUnit.MINUTES.toMillis(30);

    int maxMemberUserQuantityInReceptionist = 3;
    int maxGroupUserQuantityInReceptionist = 10;
    int maxRecentMessageBufferSize = 10;
    int maxUserAttributeQuantity = 20;
    int maxReceptionistQuantity = 50;

    long sendMessagePeriod = TimeUnit.SECONDS.toMillis(3);
}
