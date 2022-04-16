package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.Bot;

/**
 * 组件尚未完成异常
 *
 * @author Chuanwise
 */
public class NotYetImplementedException
    extends BotRuntimeException {
    
    public NotYetImplementedException(Bot bot) {
        super(bot);
    }
    
    public NotYetImplementedException(Bot bot, Throwable cause) {
        super(bot, cause);
    }
    
    public NotYetImplementedException(Bot bot, String message) {
        super(bot, message);
    }
    
    public NotYetImplementedException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
    }
}
