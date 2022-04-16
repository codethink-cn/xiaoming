package cn.codethink.xiaoming.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @see cn.codethink.xiaoming.event.CancellableEvent
 * @author Chuanwise
 */
@Data
public abstract class AbstractCancellableEvent
    extends AbstractEvent
    implements CancellableEvent {
    
    /**
     * 事件是否被取消
     */
    private volatile boolean cancelled;
    
    /**
     * 打断是否是因为取消
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    volatile boolean interruptCausedByCancelling;
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public final boolean uncancel() {
        if (intercepted) {
            return false;
        }
        if (!cancelled) {
            return false;
        }
        cancelled = false;
        return true;
    }
    
    @Override
    public final boolean cancel(boolean interrupt) {
        if (intercepted || cancelled) {
            return false;
        }
        cancelled = true;
    
        if (Objects.nonNull(thread) && interrupt) {
            try {
                interruptCausedByCancelling = true;
                
                thread.interrupt();
            } finally {
                interruptCausedByCancelling = false;
            }
        }
        
        return true;
    }
}
