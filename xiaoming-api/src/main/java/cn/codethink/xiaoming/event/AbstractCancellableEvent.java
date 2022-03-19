package cn.codethink.xiaoming.event;

import lombok.Data;

/**
 * @see cn.codethink.xiaoming.event.CancellableEvent
 * @author Chuanwise
 */
@Data
public abstract class AbstractCancellableEvent
        implements CancellableEvent {
    
    /**
     * 事件是否被取消
     */
    protected volatile boolean cancelled;
}
