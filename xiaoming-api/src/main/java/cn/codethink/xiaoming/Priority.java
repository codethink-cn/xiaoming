package cn.codethink.xiaoming;

import cn.codethink.xiaoming.event.Listener;

/**
 * 用来表示某种优先级
 *
 * @see Listener
 * @author Chuanwise
 */
public enum Priority {
    
    /**
     * 最低
     */
    LOWEST,
    
    /**
     * 低
     */
    LOW,
    
    /**
     * 普通
     */
    NORMAL,
    
    /**
     * 高
     */
    HIGH,
    
    /**
     * 最高
     */
    HIGHEST
}
