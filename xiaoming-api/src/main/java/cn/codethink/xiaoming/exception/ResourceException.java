package cn.codethink.xiaoming.exception;

import java.io.IOException;

/**
 * 资源相关的异常，如反序列化错误等。
 *
 * @author Chuanwise
 */
public class ResourceException
    extends IOException {
    
    public ResourceException() {
    }
    
    public ResourceException(String message) {
        super(message);
    }
    
    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceException(Throwable cause) {
        super(cause);
    }
}
