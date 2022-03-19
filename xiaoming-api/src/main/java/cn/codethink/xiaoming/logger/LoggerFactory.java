package cn.codethink.xiaoming.logger;

import cn.codethink.common.util.Maps;
import cn.codethink.common.util.Preconditions;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局日志工厂
 *
 * @author Chuanwise
 */
public abstract class LoggerFactory {
    
    /**
     * 保存现有日志的哈希表
     */
    private static final Map<String, Logger> LOGGERS = new ConcurrentHashMap<>();
    
    /**
     * 获取所有现有的 Logger
     *
     * @return 现有的 Logger
     */
    public static Map<String, Logger> getLoggers() {
        return Collections.unmodifiableMap(LOGGERS);
    }
    
    /**
     * 获得一个日志
     * @param name 日志名
     * @return 具备该名字的日志
     */
    public Logger getLogger(String name) {
        Preconditions.namedArgumentNonEmpty(name, "logger name");
    
        return Maps.getOrPutGet(LOGGERS, name, () -> generateLogger(name));
    }
    
    /**
     * 生成 Logger
     *
     * @param name 日志名
     * @return Logger
     */
    protected abstract Logger generateLogger(String name);
}
