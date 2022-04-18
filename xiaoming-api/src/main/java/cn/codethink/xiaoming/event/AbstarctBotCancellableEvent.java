package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import lombok.Data;

/**
 * @see cn.codethink.xiaoming.event.CancellableEvent
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public abstract class AbstarctBotCancellableEvent
    extends AbstractBotEvent
    implements CancellableEvent {
    
    protected volatile boolean cancelled;
    
    public AbstarctBotCancellableEvent(Bot bot) {
        super(bot);
    }
}
