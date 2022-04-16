package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Stranger;

/**
 * 和陌生人相关的事件
 *
 * @author Chuanwise
 */
public interface StrangerEvent
    extends Event {
    
    /**
     * 获取陌生人
     *
     * @return 陌生人
     */
    Stranger getStranger();
}
