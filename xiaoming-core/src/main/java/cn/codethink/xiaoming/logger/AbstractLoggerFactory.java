package cn.codethink.xiaoming.logger;

import cn.chuanwise.common.util.Maps;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.logger.LoggerFactory
 */
public abstract class AbstractLoggerFactory
    implements LoggerFactory {
    
    private final Map<String, Logger> loggers = new WeakHashMap<>();
    
    @Override
    public Logger getLogger(String name) {
        return Maps.getOrPutGet(loggers, name, () -> getLogger0(name));
    }
    
    /**
     * 构造日志
     *
     * @param name 日志名
     * @return 日志
     */
    protected abstract Logger getLogger0(String name);
}
