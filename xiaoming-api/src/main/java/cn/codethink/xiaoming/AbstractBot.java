package cn.codethink.xiaoming;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.concurrent.Scheduler;
import cn.codethink.xiaoming.configuration.CoreConfiguration;
import cn.codethink.xiaoming.event.EventManager;
import cn.codethink.xiaoming.logger.Logger;
import cn.codethink.xiaoming.logger.LoggerFactory;
import cn.codethink.xiaoming.message.ResourcePool;
import lombok.Data;

import java.util.Objects;

/**
 * @see Bot
 * @author Chuanwise
 */
@Data
public abstract class AbstractBot
        implements Bot {
    
    /**
     * 机器人状态
     */
    protected volatile State state = State.IDLE;
    
    /**
     * 核心配置信息
     */
    protected CoreConfiguration coreConfiguration = new CoreConfiguration();
    
    @Override
    public void setCoreConfiguration(CoreConfiguration coreConfiguration) {
        Preconditions.namedArgumentNonNull(coreConfiguration, "core configuration");
        
        this.coreConfiguration = coreConfiguration;
    }
    
    /**
     * 核心调度器
     */
    protected Scheduler scheduler;
    
    /**
     * 资源池
     */
    protected ResourcePool resourcePool;
    
    /**
     * 日志记录器
     */
    protected Logger logger;
    
    /**
     * 事件管理器
     */
    protected EventManager eventManager;
    
    public AbstractBot() {
        // 关闭 jvm 时如果还没有关闭 Bot，则关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            switch (state) {
                case IDLE:
                    break;
                case STARTED:
                case STARTING:
                case STOP_ERROR:
                    stop();
                    break;
                case STOPPING:
                    break;
                case FATAL_ERROR:
                case START_ERROR:
                default:
                    throw new IllegalStateException();
            }
        }));
    }
}