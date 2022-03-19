package cn.codethink.xiaoming.logger;

/**
 * 简单日志工厂
 *
 * @author Chuanwise
 * @see LoggerFactory
 */
public class SimpleLoggerFactory
        extends LoggerFactory {

    /** @see LoggerFactory#getLogger(String) */
    @Override
    public Logger generateLogger(String name) {
        return new SimpleLogger(name);
    }
}
