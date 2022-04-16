package cn.codethink.xiaoming.logger;

import cn.codethink.common.util.Preconditions;
import lombok.Data;

/**
 * 抽象日志工具
 * @see Logger
 * @author Chuanwise
 */
@Data
public abstract class AbstractLogger
        implements Logger {

    /** 日志名 */
    protected String name;

    /** 日志等级 */
    protected LoggerLevel level = LoggerLevel.INFO;

    public AbstractLogger(String name) {
        Preconditions.objectArgumentNonEmpty(name, "logger name");

        this.name = name;
    }

    public AbstractLogger(String name, LoggerLevel level) {
        Preconditions.objectArgumentNonEmpty(name, "logger name");
        Preconditions.nonNull(level, "logger level");

        this.name = name;
        this.level = level;
    }

    /**
     * 设置日志名
     * @param name 日志名
     */
    public void setName(String name) {
        Preconditions.objectArgumentNonEmpty(name, "logger name");

        this.name = name;
    }

    /**
     * 设置日志等级
     * @param level 日志等级
     */
    @Override
    public void setLevel(LoggerLevel level) {
        Preconditions.nonNull(level, "logger level");

        this.level = level;
    }
}
