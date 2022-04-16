package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.contact.Sender;

/**
 * 和消息相关的事件
 *
 * @author Chuanwise
 */
public interface MessageEvent
    extends BotObject, Event {
    
    /**
     * 获取消息
     *
     * @return 消息
     */
    Message getMessage();
}
