package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.Message;

/**
 * 被动收到消息事件
 *
 * @author Chuanwise
 */
public interface MessageEvent
        extends BotObject {
    
    /**
     * 获取消息
     *
     * @return 消息
     */
    Message getMessage();
    
    /**
     * 获取发送方
     *
     * @return 发送方
     */
    Contact getSender();
    
    /**
     * 获取发信时间戳
     *
     * @return 发信时间戳
     */
    long getTimeMillis();
}
