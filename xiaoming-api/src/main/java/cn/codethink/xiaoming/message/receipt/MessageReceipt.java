package cn.codethink.xiaoming.message.receipt;

import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.reference.OutgoingOnlineMessageReference;

/**
 * 消息回执，调用 {@link cn.codethink.xiaoming.contact.Contact#sendMessage(Message)} 后获得。
 *
 * @author Chuanwise
 */
public interface MessageReceipt {
    
    /**
     * 获取发送后的消息
     *
     * @return 发送后的消息
     */
    Message getMessage();
    
    /**
     * 获取消息引用
     *
     * @return 消息引用
     */
    OutgoingOnlineMessageReference getMessageReference();
}
