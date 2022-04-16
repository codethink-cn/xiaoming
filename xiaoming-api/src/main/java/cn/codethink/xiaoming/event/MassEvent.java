package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.contact.Mass;

/**
 * 和集体有关的事件
 *
 * @author Chuanwise
 */
public interface MassEvent
    extends Event {
    
    /**
     * 获取集体
     *
     * @return 集体
     */
    Mass getMass();
}
