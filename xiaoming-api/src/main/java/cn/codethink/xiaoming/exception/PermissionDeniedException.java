package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.Bot;

/**
 * 缺少权限异常。
 * 当 Bot 缺少权限进行某些操作时抛出。
 *
 * @author Chuanwise
 */
public class PermissionDeniedException
    extends BotRuntimeException {
    
    private static final String DEFAULT_MESSAGE = "permission denied";
    
    public PermissionDeniedException(Bot bot) {
        super(bot, DEFAULT_MESSAGE);
    }
    
    public PermissionDeniedException(Bot bot, Throwable cause) {
        super(bot, DEFAULT_MESSAGE, cause);
    }
    
    public PermissionDeniedException(Bot bot, String message) {
        super(bot, message);
    }
    
    public PermissionDeniedException(Bot bot, String message, Throwable cause) {
        super(bot, message, cause);
    }
}
