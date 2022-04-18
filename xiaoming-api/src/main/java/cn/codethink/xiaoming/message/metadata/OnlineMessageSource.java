package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.contact.ContactOrBot;
import cn.codethink.xiaoming.contact.Sender;

/**
 * 在线消息源，是 Bot 发送或接收到的消息。
 *
 * @author Chuanwise
 */
public interface OnlineMessageSource
    extends MessageSource, BotObject {
    
    /**
     * 获取消息发送方
     *
     * @return 获取消息发送方。无法获取时返回 null
     */
    Sender getSender();
    
    /**
     * 获取消息目标会话
     *
     * @return 获取消息目标会话。无法获取时返回 null
     */
    ContactOrBot getTarget();
    
    /**
     * 撤回消息
     *
     * @return 是否成功撤回消息
     * @throws cn.codethink.xiaoming.exception.PermissionDeniedException 缺少权限
     */
    boolean recall();
}
