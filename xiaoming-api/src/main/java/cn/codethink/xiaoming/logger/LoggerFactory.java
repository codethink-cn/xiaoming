package cn.codethink.xiaoming.logger;

import cn.codethink.util.Maps;
import cn.codethink.util.Preconditions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局日志工厂
 * @author Chuanwise
 */
public abstract class LoggerFactory {

    /** 保存现有日志的哈希表 */
    private static final Map<String, Logger> LOGGERS = new ConcurrentHashMap<>();

    /** 全局的日志工厂 */
    private static LoggerFactory loggerFactory = new SimpleLoggerFactory();

    /** 获取全局日志工厂 */
    public static LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /** 设置全局的日志工厂 */
    public static void setLoggerFactory(LoggerFactory loggerFactory) {
        Preconditions.namedArgumentNonNull(loggerFactory, "logger factory");

        LoggerFactory.loggerFactory = loggerFactory;
    }

    /**
     * 构造一个指定名字的日志工具
     * @param name 日志名
     * @return 该名字的日志工具
     */
    public static Logger of(String name) {
        Preconditions.namedArgumentNonEmpty(name, "logger name");

        return findOrProduce(name);
    }

    /**
     * 构造一个指定名字和日志等级的日志工具
     * @param name 日志名
     * @param level 日志等级
     * @return 该名字的日志工具
     */
    public static Logger of(String name, LoggerLevel level) {
        Preconditions.namedArgumentNonEmpty(name, "logger name");
        Preconditions.namedArgumentNonNull(level, "logger level");

        final Logger logger = findOrProduce(name);
        logger.setLevel(level);
        return logger;
    }

    /**
     * 构造一个指定类的日志工具
     * @param loggerClass 日志类
     * @return 该类的日志工具
     */
    public static Logger of(Class<?> loggerClass) {
        Preconditions.namedArgumentNonNull(loggerClass, "logger class");

        return of(loggerClass.getSimpleName());
    }

    /**
     * 构造一个指定类和日志等级的日志工具
     * @param loggerClass 日志类
     * @param level 日志等级
     * @return 该类的日志工具
     */
    public static Logger of(Class<?> loggerClass, LoggerLevel level) {
        Preconditions.namedArgumentNonNull(loggerClass, "logger class");
        Preconditions.namedArgumentNonNull(level, "logger level");

        return of(loggerClass.getSimpleName(), level);
    }

    /**
     * 查找或用全局日志工厂生产一个日志
     * @param name 日志名
     * @return 该名字的日志工具
     */
    private static Logger findOrProduce(String name) {
        return Maps.getOrPutGet(LOGGERS, name, () -> loggerFactory.produce(name));
    }

    /**
     * 获得一个日志
     * @param name 日志名
     * @return 具备该名字的日志
     */
    public abstract Logger produce(String name);
}
