package cn.codethink.xiaoming.event;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.BotObject;
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
