package cn.codethink.xiaoming.event;

/**
 * 所有事件需要实现的接口
 *
 * @author Chuanwise
 */
public interface Event {
    
    /**
     * 询问是否有监听器正在处理事件
     *
     * @return 是否有监听器正在处理事件
     */
    boolean isHanding();
    
    /**
     * 拦截事件
     *
     * @param interrupt 是否打断正在处理的监听器
     * @return 是否成功拦截事件
     */
    boolean intercept(boolean interrupt);
    
    /**
     * 拦截事件，不打断正在处理的监听器
     *
     * @return 是否成功拦截事件
     */
    default boolean intercept() {
        return intercept(false);
    }
    
    /**
     * 询问事件是否被拦截
     *
     * @return 事件是否被拦截
     */
    boolean isIntercepted();
}
