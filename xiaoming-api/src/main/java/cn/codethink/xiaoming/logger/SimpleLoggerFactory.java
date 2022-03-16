package cn.codethink.xiaoming.logger;

/**
 * 简单日志工厂
 * @see LoggerFactory
 */
public class SimpleLoggerFactory
        extends LoggerFactory {

    /** @see LoggerFactory#produce(String) */
    @Override
    public Logger produce(String name) {
        return new SimpleLogger(name);
    }
}
