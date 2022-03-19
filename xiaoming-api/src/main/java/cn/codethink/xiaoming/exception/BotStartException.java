package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.Bot;

/**
 * Bot 启动时异常
 *
 * @author Chuanwise
 */
public class BotStartException
        extends BotNestedRuntimeException {
    
    public BotStartException(Bot bot, Throwable cause) {
        super(bot, cause);
    }
    
    public BotStartException(Bot bot) {
        super(bot);
    }
    
    public BotStartException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
    }
}
