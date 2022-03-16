package cn.codethink.xiaoming.exception;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.Bot;

/**
 * Bot 的嵌套异常，cause 必定非 null
 *
 * @author Chuanwise
 */
public class BotNestedRuntimeException
        extends BotRuntimeException
        implements BotNestedThrowable {
    
    public BotNestedRuntimeException(Bot bot, Throwable cause) {
        super(bot, cause);
    
        Preconditions.namedArgumentNonNull(cause, "cause");
    }
    
    public BotNestedRuntimeException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
        
        Preconditions.namedArgumentNonNull(cause, "cause");
    }
}
