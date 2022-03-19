package cn.codethink.xiaoming.event;

import cn.codethink.common.api.ExceptionConsumer;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Priority;
import lombok.Data;

/**
 * 简单的监听器
 *
 * @param <T> 事件类型
 */
@Data
@SuppressWarnings("all")
public class SimpleListener<T>
        extends AbstractListener {
    
    private final ExceptionConsumer<T> action;
    
    public SimpleListener(Class<T> eventClass, Priority priority, boolean alwaysValid, ExceptionConsumer<T> action) {
        super(eventClass, priority, alwaysValid);
    
        Preconditions.namedArgumentNonNull(action, "action");
        
        this.action = action;
    }
    
    @Override
    public boolean handleEvent(Object event) throws Exception {
        if (!eventClass.isInstance(event)) {
            return false;
        }
        
        action.exceptAccept((T) event);
    
        return true;
    }
}
