package cn.codethink.xiaoming.exception;

/**
 * 异常处理器
 *
 * @author Chuanwise
 */
public interface ExceptionHandler {
    
    /**
     * 处理异常
     *
     * @param throwable
     */
    void handleException(Throwable throwable);
}
