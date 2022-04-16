package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.exception.BotRuntimeException;

/**
 * 因取消导致的异常。
 *
 * 通常是指事件被取消导致。
 *
 * @author Chuanwise
 */
public class CancelledException
    extends BotRuntimeException {
    
    public CancelledException(Bot bot) {
        super(bot);
    }
    
    public CancelledException(Bot bot, Throwable cause) {
        super(bot, cause);
    }
    
    public CancelledException(Bot bot, String message) {
        super(bot, message);
    }
    
    public CancelledException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
    }
}
