package cn.codethink.xiaoming.exception;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;

/**
 * Bot 的嵌套异常，cause 必定非 null
 *
 * @author Chuanwise
 */
public class BotNestedException
        extends BotException
        implements BotNestedThrowable {
    
    public BotNestedException(Bot bot, Throwable cause) {
        super(bot, cause);
    
        Preconditions.nonNull(cause, "cause");
    }
    
    public BotNestedException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
        
        Preconditions.nonNull(cause, "cause");
    }
}
