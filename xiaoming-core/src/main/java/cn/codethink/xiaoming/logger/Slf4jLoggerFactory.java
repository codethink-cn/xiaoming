package cn.codethink.xiaoming.logger;

/**
 * 通过 Slf4j 构造日志记录器
 *
 * @author Chuanwise
 */
public class Slf4jLoggerFactory
    extends LoggerFactory {
    
    @Override
    public Logger generateLogger(String name) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
    }
}
