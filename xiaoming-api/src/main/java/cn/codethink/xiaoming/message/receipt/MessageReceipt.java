package cn.codethink.xiaoming.message.receipt;

import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.OutgoingOnlineMessageSource;

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
    CompoundMessage getMessage();
    
    /**
     * 获取消息源
     *
     * @return 消息源
     */
    OutgoingOnlineMessageSource getMessageSource();
    
    /**
     * 撤回相关消息
     *
     * @return 是否成功撤回消息
     * @throws cn.codethink.xiaoming.exception.PermissionDeniedException 缺少权限
     */
    default boolean recall() {
        return getMessageSource().recall();
    }
}
