package cn.codethink.xiaoming;

/**
 * 小明状态
 *
 * @author Chuanwise
 */
public enum State {
    
    /**
     * 尚未启动的状态
     */
    IDLE,
    
    /**
     * 正在启动状态
     */
    STARTING,
    
    /**
     * 启动时出现错误状态
     */
    START_ERROR,
    
    /**
     * 已启动状态
     */
    STARTED,
    
    /**
     * 正在关闭状态
     */
    STOPPING,
    
    /**
     * 关闭时出现错误状态
     */
    STOP_ERROR,
    
    /**
     * 严重错误状态
     */
    FATAL_ERROR,
}
