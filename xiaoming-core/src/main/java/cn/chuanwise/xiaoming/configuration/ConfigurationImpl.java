package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.annotation.Experimental;
import cn.chuanwise.annotation.Incomplete;
import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.toolkit.serialize.serializer.configuration.SerializerConfiguration;
import cn.chuanwise.toolkit.serialize.serializer.json.configuration.JsonSerializerConfiguration;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.limit.CallLimitConfiguration;
import cn.chuanwise.xiaoming.limit.CallLimitConfigurationImpl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
public class ConfigurationImpl extends FilePreservableImpl implements Configuration {
    transient XiaomingBot xiaomingBot;

    boolean debug = false;
    boolean enablePreviewFunctions = false;

    CallLimitConfiguration groupCallConfig = new CallLimitConfigurationImpl();
    CallLimitConfiguration privateCallConfig = new CallLimitConfigurationImpl();

    int maxIterateTime = 20;

    /**
     * 存储使用的数据交换语言类型。可选 JSON、YAML 和 XML
     * 这个配置项只能修改自定义的部分，核心配置仍然是 JSON。
     */
    @Experimental
    @Incomplete
    DelType storageDelType = DelType.JSON;

    /** 存储数据所用编码类型 */
    String storageEncoding = "UTF-8";
    String storageDecoding = "UTF-8";

    /** 自动同意添加好友、加群申请 */
    boolean autoAcceptFriendAddRequest = true;
    boolean autoAcceptGroupInvite = false;

    /** 主线程池最大容量 */
    int maxMainThreadPoolSize = 5;

    /** 接待员线程池最大容量 */
    int maxReceptionThreadPoolSize = 3;

    /** 启动用户使用验证 */
    boolean enableLicense = false;

    /** 序列化设置 */
    SerializerConfiguration serializerConfiguration = new JsonSerializerConfiguration();

    /** 启动时在日志群发消息 */
    boolean enableStartLog = false;

    /** 启动明确调用 */
    boolean enableClearCall = false;

    boolean saveFileDirectly = true;

    /** 合规明确调用头 */
    Set<String> clearCallPrefixes = new LinkedHashSet<>();

    /** 和用户 */
    long maxUserInputWaitTime = TimeUnit.MINUTES.toMillis(10);
    long maxUserGlobalInputWaitTime = TimeUnit.MINUTES.toMillis(10);
    long maxUserPrivateInputWaitTime = TimeUnit.MINUTES.toMillis(10);
    long maxUserGroupInputWaitTime = TimeUnit.MINUTES.toMillis(10);

    long optimizePeriod = TimeUnit.MINUTES.toMillis(10);
    long savePeriod = TimeUnit.MINUTES.toMillis(30);

    /** 特殊功能标记 */
    String enableGroupTag = "enable";
    String quietModeGroupTag = "quiet";
    String clearCallGroupTag = "clear-call";

    String blockPluginTagPrefix = "plugin.block.";

    int maxRecentMessageBufferSize = 10;
    int maxRecentGroupMessageBufferQuantity = 10;
    int maxLoadedAccountQuantity = 10;
    int maxUserRecentGroupMessageBufferQuantity = 3;
    int maxRecentGroupMemberMessageBufferQuantity = 10;
    int maxRecentPrivateMessageBufferQuantity = 3;
    int maxGroupUserQuantityInReceptionist = 10;
    int maxMemberUserQuantityInReceptionist = 5;
    int maxUserPropertyQuantity = 20;
    int maxRecentPrivateMessageBufferSize = 10;
    int maxReceptionistQuantity = 50;
}
