package cn.codethink.xiaoming.logger;

import cn.codethink.common.util.Exceptions;

/**
 * 日志工具
 * @author Chuanwise
 */
public interface Logger {

    /**
     * 记录一条轨迹日志信息
     * @param message 轨迹日志信息
     * @param arguments 日志中使用 {} 引用的变量列表
     */
    default void trace(Object message, Object... arguments) {
        log(LoggerLevel.TRACE, message, arguments);
    }

    /**
     * 记录一条轨迹日志信息
     * @param message 轨迹日志信息
     * @param cause 异常
     * @throws IllegalArgumentException cause 为 null 时
     */
    default void trace(Object message, Throwable cause) {
        trace(message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /**
     * 记录一条普通日志信息
     * @param message 普通日志信息
     * @param arguments 日志中使用 {} 引用的变量列表
     */
    default void info(Object message, Object... arguments) {
        log(LoggerLevel.INFO, message, arguments);
    }

    /**
     * 记录一条普通日志信息
     * @param message 普通日志信息
     * @param cause 异常
     * @throws IllegalArgumentException cause 为 null 时
     */
    default void info(Object message, Throwable cause) {
        info(message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /**
     * 记录一条警告日志信息
     * @param message 警告日志信息
     * @param arguments 日志中使用 {} 引用的变量列表
     */
    default void warn(Object message, Object... arguments) {
        log(LoggerLevel.WARN, message, arguments);
    }

    /**
     * 记录一条警告异常信息
     * @param message 警告日志信息
     * @param cause 异常
     * @throws IllegalArgumentException cause 为 null 时
     */
    default void warn(Object message, Throwable cause) {
        warn(message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /**
     * 记录一条错误异常信息
     * @param message 错误日志信息
     * @param cause 异常
     * @throws IllegalArgumentException cause 为 null 时
     */
    default void error(Object message, Throwable cause) {
        error(message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /**
     * 记录一条错误日志信息
     * @param message 错误日志信息
     * @param arguments 日志中使用 {} 引用的变量列表
     */
    default void error(Object message, Object... arguments) {
        log(LoggerLevel.ERROR, message, arguments);
    }

    /**
     * 记录一条调试异常信息
     * @param message 调试日志信息
     * @param cause 异常
     * @throws IllegalArgumentException cause 为 null 时
     */
    default void debug(Object message, Throwable cause) {
        debug(message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /**
     * 记录一条调试日志信息
     * @param message 调试日志信息
     * @param arguments 日志中使用 {} 引用的变量列表
     */
    default void debug(Object message, Object... arguments) {
        log(LoggerLevel.DEBUG, message, arguments);
    }

    /**
     * 记录一条严重错误异常信息
     * @param message 严重错误日志信息
     * @param cause 异常
     * @throws IllegalArgumentException cause 为 null 时
     */
    default void fatal(Object message, Throwable cause) {
        fatal(message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /**
     * 记录一条严重错误日志信息
     * @param message 严重错误日志信息
     * @param arguments 日志中使用 {} 引用的变量列表
     */
    default void fatal(Object message, Object... arguments) {
        log(LoggerLevel.FATAL, message, arguments);
    }

    /**
     * 发送一条指定日志等级的日志消息
     * @param level 日志等级
     * @param message 日志消息
     * @param arguments 日志消息中使用 {} 引用的变量列表
     */
    void log(LoggerLevel level, Object message, Object... arguments);

    /**
     * 发送一条指定日志等级的日志消息
     * @param level 日志等级
     * @param message 日志消息
     * @param cause 异常
     */
    default void log(LoggerLevel level, Object message, Throwable cause) {
        log(level, message + "\n" + Exceptions.writeStackTrace(cause));
    }

    /** 获取日志等级 */
    LoggerLevel getLevel();

    /** 设置日志等级 */
    void setLevel(LoggerLevel level);
}
