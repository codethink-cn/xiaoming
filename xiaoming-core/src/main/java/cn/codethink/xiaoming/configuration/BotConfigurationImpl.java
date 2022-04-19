package cn.codethink.xiaoming.configuration;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.logger.LoggerFactory;
import cn.codethink.xiaoming.logger.Slf4jLoggerFactory;
import cn.codethink.xiaoming.protocol.Protocol;
import lombok.Data;

import java.io.File;
import java.util.Objects;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.configuration.BotConfiguration
 */
@Data
public class BotConfigurationImpl
    implements BotConfiguration {
    
    private static final String DEVICE_INFO_FILE_NAME = "device.json";
    
    /**
     * 核心线程池大小
     */
    protected int threadCount = 20;
    
    /**
     * 工作目录
     */
    protected File workingDirectory = new File(System.getProperty("user.dir"));
    
    /**
     * 设备信息文件，null 表示不启用
     */
    protected File deviceInfoFile = new File(workingDirectory, DEVICE_INFO_FILE_NAME);
    
    /**
     * 登录协议
     */
    protected Protocol protocol = Protocol.ANDROID_PHONE;
    
    @Override
    public void setWorkingDirectory(File workingDirectory) {
        Preconditions.nonNull(workingDirectory, "working directory");
    
        if (Objects.equals(workingDirectory, this.workingDirectory)) {
            return;
        }
        if (Objects.equals(new File(this.workingDirectory, DEVICE_INFO_FILE_NAME), deviceInfoFile)) {
            deviceInfoFile = new File(workingDirectory, DEVICE_INFO_FILE_NAME);
        }
        
        this.workingDirectory = workingDirectory;
    }
    
    /**
     * 隐藏实现框架的 Log
     */
    protected boolean hideImplementBotLog = true;
    
    /**
     * Log 工厂
     */
    protected LoggerFactory loggerFactory = new Slf4jLoggerFactory();
}
