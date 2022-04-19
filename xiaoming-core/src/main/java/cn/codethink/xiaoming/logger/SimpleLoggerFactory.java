package cn.codethink.xiaoming.logger;

/**
 * 简单日志工厂
 *
 * @author Chuanwise
 * @see LoggerFactory
 */
public class SimpleLoggerFactory
    extends AbstractLoggerFactory {
    
    @Override
    protected Logger getLogger0(String name) {
        return new SimpleLogger(name);
    }
}
