package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.Bot;

/**
 * Bot 关闭时异常
 *
 * @author Chuanwise
 */
public class BotStopException
        extends BotNestedRuntimeException {
    
    public BotStopException(Bot bot, Throwable cause) {
        super(bot, cause);
    }
    
    public BotStopException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
    }
}
