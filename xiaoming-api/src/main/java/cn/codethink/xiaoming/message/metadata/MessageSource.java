package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.property.Property;

/**
 * <h1>消息源</h1>
 *
 * <p>用来精确定位一条消息。</p>
 *
 * <h2>{@link OnlineMessageSource} - 在线消息源</h2>
 *
 * <p>消息源可能是和 Bot 相关的，如 Bot 收到、Bot 发送的消息。这类消息源是在线消息源 {@link OnlineMessageSource}。
 * 可以打开相关会话和对象（{@link OnlineMessageSource#getSender()} 和 {@link OnlineMessageSource#getTarget()}）。</p>
 *
 * <p>在线消息源是平台相关的，序列化规范是 {@code [source:core,arguments...]}。例如在 qq 平台上可以序列化为
 * {@code [source:qq,online,outgoing,friend,$botId,$timestamp,$ids,$internalIds,$fromId,$toId,$message]}</p>。
 *
 * <h2>{@link OfflineMessageSource} - 离线消息源</h2>
 *
 * <p>通过合并转发等方式获取的消息源，是离线消息源 {@link OfflineMessageSource}。可能相关人员不是 Bot 的好友或所在群的
 * 群员，或消息源是伪造的，因此不一定能打开相关会话和对象。</p>
 *
 * @author Chuanwise
 */
@IMRelatedAPI
public interface MessageSource
    extends MessageMetadata {
    
    @Override
    @SuppressWarnings("all")
    default Property<MessageSource> getMetadataType() {
        return MessageMetadataType.SOURCE;
    }
    
    /**
     * 获取消息源类型
     *
     * @return 消息源类型
     */
    MessageSourceType getMessageSourceType();
    
    /**
     * 获取 Bot 的编号
     *
     * @return Bot 的编号
     */
    Code getBotCode();
    
    /**
     * 获取消息源编号
     *
     * @return 消息源编号
     */
    Code getSourceCode();
    
    /**
     * 获取消息目标编号
     *
     * @return 消息目标编号
     */
    Code getTargetCode();
    
    /**
     * 获取消息时间戳
     *
     * @return 消息时间戳
     */
    long getTimestamp();
    
    /**
     * 获取消息
     *
     * @return 消息
     */
    Message getMessage();
}
