package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Contact;

/**
 * Bot 发消息事件
 *
 * @author Chuanwise
 */
public interface SendMessageEvent
    extends MessageEvent {
    
    /**
     * 获取消息接收者
     *
     * @return 消息接收者
     */
    Contact getTarget();
}
