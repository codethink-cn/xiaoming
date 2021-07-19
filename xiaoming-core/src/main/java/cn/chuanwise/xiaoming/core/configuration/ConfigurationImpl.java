package cn.chuanwise.xiaoming.core.configuration;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.configuration.Configuration;
import cn.chuanwise.xiaoming.api.configuration.DelType;
import cn.chuanwise.xiaoming.core.limit.CallLimitConfigImpl;
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

    CallLimitConfigImpl groupCallConfig = new CallLimitConfigImpl();
    CallLimitConfigImpl privateCallConfig = new CallLimitConfigImpl();

    int maxIterateTime = 20;

    /**
     * 存储使用的数据交换语言类型。可选 JSON、YAML 和 XML
     * 这个配置项只能修改自定义的部分，核心配置仍然是 JSON。
     */
    DelType storageDelType = DelType.JSON;

    /** 存储数据所用编码类型 */
    String storageEncoding = "UTF-8";

    /** 自动同意添加好友申请 */
    boolean autoAcceptFriendAddRequest = true;

    /** 主线程池最大容量 */
    int maxMainThreadPoolSize = 5;

    /** 接待员线程池最大容量 */
    int maxReceptionThreadPoolSize = 3;

    /** 启动用户使用验证 */
    boolean enableLicense = false;

    /** 启动时在日志群发消息 */
    boolean enableStartLog = false;

    /** 启动明确调用 */
    boolean enableClearCall = false;

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
}
