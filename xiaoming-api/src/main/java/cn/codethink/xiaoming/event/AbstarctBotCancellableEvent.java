package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.AbstractBotObject;
import lombok.Data;

/**
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public abstract class AbstarctBotCancellableEvent
        extends AbstractBotObject
        implements CancellableEvent {
    
    protected volatile boolean cancelled;
    
    public AbstarctBotCancellableEvent(Bot bot) {
        super(bot);
    }
}
