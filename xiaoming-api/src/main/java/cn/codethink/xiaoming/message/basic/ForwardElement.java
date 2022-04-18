package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.IMRelatedAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.metadata.OnlineMessageSource;
import cn.codethink.xiaoming.spi.XiaoMing;
import cn.codethink.xiaoming.message.Message;

/**
 * <h1>合并转发单元</h1>
 *
 * <p>表示一个合并转发的消息内容。</p>
 *
 * @author Chuanwise
 *
 * @see Forward
 * @see ForwardBuilder
 */
@IMRelatedAPI
public interface ForwardElement {
    
    /**
     * 构造转发消息单元
     *
     * @param senderCode 发送者账户号
     * @param senderName 发送者名
     * @param timestamp 时间戳
     * @param message 消息
     * @return 转发消息单元
     * @throws NullPointerException senderCode, senderName 或 message 为 null
     * @throws IllegalArgumentException senderName 为 ""
     */
    static ForwardElement newInstance(Code senderCode, String senderName, long timestamp, Message message) {
        Preconditions.objectNonNull(senderCode, "sender code");
        Preconditions.objectArgumentNonEmpty(senderName, "sender name");
        Preconditions.objectNonNull(message, "message");
    
        return XiaoMing.get().newForwardElement(senderCode, senderName, timestamp, message);
    }
    
    /**
     * 用消息源构建转发消息单元
     *
     * @param reference 消息源
     * @return 转发消息单元
     * @throws NullPointerException reference 为 null
     */
    static ForwardElement of(OnlineMessageSource reference) {
        Preconditions.objectNonNull(reference, "reference");
    
        return newInstance(reference.getSourceCode(), reference.getSender().getSenderName(), reference.getTimestamp(), reference.getMessage());
    }
    
    /**
     * 获取发送方账户号
     *
     * @return 发送方账户号
     */
    Code getSenderCode();
    
    /**
     * 获取发送方名称
     *
     * @return 发送方名称
     */
    String getSenderName();
    
    /**
     * 获取消息发送时间戳
     *
     * @return 消息发送时间戳
     */
    long getTimestamp();
    
    /**
     * 获取消息内容。
     *
     * 消息可能是伪造的，因此不一定是 {@link cn.codethink.xiaoming.message.compound.CompoundMessage}
     *
     * @return 消息内容
     */
    Message getMessage();
}
