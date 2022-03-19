package cn.codethink.xiaoming.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.NoSuchElementException;

/**
 * 日志等级
 * @author Chuanwise
 */
@Getter
@AllArgsConstructor
public enum LoggerLevel {
    
    /**
     * 轨迹
     */
    TRACE(0, "轨迹"),
    
    /**
     * 信息
     */
    INFO(2, "信息"),
    
    /**
     * 警告
     */
    WARN(3, "警告"),
    
    /**
     * 错误
     */
    ERROR(4, "错误"),
    
    /**
     * 调试
     */
    DEBUG(1, "调试"),
    
    /**
     * 严重错误
     */
    FATAL(5, "严重错误");

    private final int level;
    
    private final String name;

    /**
     * 获取指定日志等级的日志等级
     * @param level 日志数字等级
     * @return 日志等级
     */
    public static LoggerLevel of(int level) {
        for (LoggerLevel value : values()) {
            if (value.level == level) {
                return value;
            }
        }
        
        throw new NoSuchElementException();
    }
}
