package cn.codethink.xiaoming.logger;

/**
 * 日志工厂
 *
 * @author Chuanwise
 */
public interface LoggerFactory {
    
    /**
     * 获得一个日志
     *
     * @param name 日志名
     * @return 具备该名字的日志
     */
    Logger getLogger(String name);
}