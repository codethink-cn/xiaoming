package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Group;

/**
 * 和群相关的事件
 *
 * @author Chuanwise
 */
public interface GroupEvent
    extends MassEvent {
    
    /**
     * 获取群
     *
     * @return 群
     */
    @Override
    Group getMass();
}