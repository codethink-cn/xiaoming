package cn.codethink.xiaoming.logger;

import org.slf4j.LoggerFactory;

/**
 * 通过 Slf4j 构造日志记录器
 *
 * @author Chuanwise
 */
public class Slf4jLoggerFactory
    extends AbstractLoggerFactory {
    
    @Override
    protected Logger getLogger0(String name) {
        return new Slf4jLogger(LoggerFactory.getLogger(name));
    }
}
