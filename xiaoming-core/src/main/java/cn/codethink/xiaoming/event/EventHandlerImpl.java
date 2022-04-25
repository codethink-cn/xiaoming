package cn.codethink.xiaoming.event;

import cn.chuanwise.common.api.ExceptionConsumer;
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
public class EventHandlerImpl<T>
    extends AbstractEventHandler {
    
    private final ExceptionConsumer<T> action;
    
    public EventHandlerImpl(Class<T> eventClass, Priority priority, boolean alwaysValid, ExceptionConsumer<T> action) {
        super(eventClass, priority, alwaysValid);
    
        Preconditions.nonNull(action, "action");
        
        this.action = action;
    }
    
    @Override
    protected boolean handleEvent0(Event event) throws Exception {
        action.exceptAccept((T) event);
        return true;
    }
}
