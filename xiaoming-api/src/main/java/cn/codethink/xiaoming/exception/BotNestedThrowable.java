package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.BotObject;

/**
 * Bot 嵌套异常
 *
 * @author Chuanwise
 */
public interface BotNestedThrowable
        extends BotObject {
    
    /**
     * 获取嵌套的异常
     *
     * @return 嵌套的异常
     */
    Throwable getCause();
}
