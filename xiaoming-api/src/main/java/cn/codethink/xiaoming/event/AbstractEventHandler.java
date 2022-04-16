package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Priority;
import lombok.Data;

/**
 * 抽象事件监听器
 *
 * @author Chuanwise
 */
@Data
public abstract class AbstractEventHandler
    implements EventHandler {
    
    protected final Class<?> eventClass;
    
    protected final Priority priority;
    
    protected final boolean alwaysValid;
    
    public AbstractEventHandler(Class<?> eventClass, Priority priority, boolean alwaysValid) {
        Preconditions.nonNull(eventClass, "event class");
        Preconditions.nonNull(priority, "priority");
        
        this.eventClass = eventClass;
        this.priority = priority;
        this.alwaysValid = alwaysValid;
    }
    
    @Override
    public boolean handleEvent(Event event) throws Exception {
        Preconditions.nonNull(event, "event");
        Preconditions.state(event instanceof AbstractEvent, "event is not a instance of AbstractEvent");
    
        final AbstractEvent abstractEvent = (AbstractEvent) event;
        Preconditions.stateIsNull(abstractEvent.thread, "event is been handing");
    
        if (!eventClass.isInstance(event)) {
            return false;
        }
        if (event.isIntercepted()) {
            return false;
        }
        if (event instanceof CancellableEvent) {
            Preconditions.state(event instanceof AbstractCancellableEvent, "event is cancellable, but is not a instance of AbstractCancellableEvent");
            if (((AbstractCancellableEvent) event).isCancelled() && !alwaysValid) {
                return false;
            }
        }
        
        try {
            abstractEvent.thread = Thread.currentThread();
    
            return handleEvent0(event);
        } catch (Throwable throwable) {
            // check if this exception is caused by interrupting
            if (throwable instanceof InterruptedException) {
                if (event instanceof AbstractCancellableEvent) {
                    final AbstractCancellableEvent abstractCancellableEvent = (AbstractCancellableEvent) event;
                    if (abstractCancellableEvent.interruptCausedByCancelling) {
                        return false;
                    }
                }
                if (abstractEvent.interruptCausedByIntercepting) {
                    return false;
                }
            }
            
            // rethrow
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            }
            
            throw new RuntimeException(throwable);
        } finally {
            abstractEvent.thread = null;
        }
    }
    
    /**
     * 真正处理事件的业务逻辑
     *
     * @param event 事件
     * @return 事件是否被处理
     * @throws Exception 处理事件中出现的异常
     */
    protected abstract boolean handleEvent0(Event event) throws Exception;
}
