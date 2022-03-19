package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Member;
import cn.codethink.xiaoming.contact.Scope;

/**
 * 来自范围的消息事件
 *
 * @author Chuanwise
 */
public interface ScopeMessageEvent
        extends MessageEvent {
    
    /**
     * 获取消息发送者
     *
     * @return 消息发送者
     */
    @Override
    Member getSender();
    
    /**
     * 获取消息所在的范围
     *
     * @return 消息所在的范围
     */
    Scope getScope();
}
