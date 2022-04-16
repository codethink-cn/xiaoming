package cn.codethink.xiaoming.event;

import java.util.Objects;

/**
 * @see cn.codethink.xiaoming.event.Event
 * @author Chuanwise
 */
public abstract class AbstractEvent
    implements Event {
    
    volatile boolean intercepted;
    
    volatile Thread thread;
    
    volatile boolean interruptCausedByIntercepting = false;
    
    @Override
    public final boolean intercept(boolean interrupt) {
        if (intercepted) {
            return false;
        }
        intercepted = true;
    
        if (Objects.nonNull(thread) && interrupt) {
            try {
                interruptCausedByIntercepting = true;
                
                thread.interrupt();
            } finally {
                interruptCausedByIntercepting = false;
            }
        }
        return true;
    }
    
    @Override
    public final boolean isIntercepted() {
        return intercepted;
    }
    
    @Override
    public final boolean isHanding() {
        return Objects.nonNull(thread);
    }
}
