package cn.codethink.xiaoming.event;

/**
 * 可以被取消的事件
 *
 * @author Chuanwise
 */
public interface CancellableEvent
    extends Event {
    
    /**
     * 判断事件是否被取消
     *
     * @return 事件是否被取消
     */
    boolean isCancelled();
    
    /**
     * 重新激活事件
     *
     * @return 是否成功重新激活事件
     */
    boolean uncancel();
    
    /**
     * 取消事件
     *
     * @param interrupt 是否打断正在执行的监听器
     * @return 是否成功取消事件
     */
    boolean cancel(boolean interrupt);
    
    /**
     * 取消事件
     *
     * @return 是否成功取消事件
     */
    default boolean cancel() {
        return cancel(false);
    }
}
