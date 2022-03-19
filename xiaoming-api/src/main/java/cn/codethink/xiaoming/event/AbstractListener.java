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
public abstract class AbstractListener
        implements Listener {
    
    protected final Class<?> eventClass;
    
    protected final Priority priority;
    
    protected final boolean alwaysValid;
    
    public AbstractListener(Class<?> eventClass, Priority priority, boolean alwaysValid) {
        Preconditions.namedArgumentNonNull(eventClass, "event class");
        Preconditions.namedArgumentNonNull(priority, "priority");
        
        this.eventClass = eventClass;
        this.priority = priority;
        this.alwaysValid = alwaysValid;
    }
}
