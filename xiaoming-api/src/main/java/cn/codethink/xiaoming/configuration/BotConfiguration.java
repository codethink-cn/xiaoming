package cn.codethink.xiaoming.configuration;

import cn.codethink.xiaoming.logger.LoggerFactory;
import cn.codethink.xiaoming.protocol.Protocol;

import java.io.File;

/**
 * 机器人配置
 *
 * @author Chuanwise
 */
public interface BotConfiguration {
    
    /**
     * 设置机器人工作目录，修改后重启生效。
     *
     * @param workingDirectory 机器人工作目录
     */
    void setWorkingDirectory(File workingDirectory);
    
    /**
     * 获取机器人工作目录
     *
     * @return 机器人工作目录
     */
    File getWorkingDirectory();
    
    /**
     * 获取核心线程池线程数
     *
     * @return 核心线程池线程数
     */
    int getThreadCount();
    
    /**
     * 设置核心线程池线程数，修改后重启生效
     *
     * @param threadCount 核心线程池线程数
     */
    void setThreadCount(int threadCount);
    
    /**
     * 获取设备信息文件
     *
     * @return 设备信息文件
     */
    File getDeviceInfoFile();
    
    /**
     * 设置设备信息文件，修改后重启生效
     *
     * @param deviceInfoFile 设备信息文件
     */
    void setDeviceInfoFile(File deviceInfoFile);
    
    /**
     * 获取登录协议
     *
     * @return 登录协议
     */
    Protocol getProtocol();
    
    /**
     * 设置登录协议，修改后重启生效
     *
     * @param protocol 登录协议
     */
    void setProtocol(Protocol protocol);
    
    /**
     * 是否隐藏实现机器人的日志，修改后重启生效
     *
     * @return 隐藏实现机器人的日志
     */
    boolean isHideImplementBotLog();
    
    /**
     * 设置是否隐藏实现机器人的日志，修改后重启生效
     *
     * @param hideImplementBotLog 是否隐藏实现机器人的日志
     */
    void setHideImplementBotLog(boolean hideImplementBotLog);
    
    /**
     * 获取日志工厂
     *
     * @return 日志工厂
     */
    LoggerFactory getLoggerFactory();
    
    /**
     * 设置日志工厂
     *
     * @param loggerFactory 日志工厂
     */
    void setLoggerFactory(LoggerFactory loggerFactory);
}