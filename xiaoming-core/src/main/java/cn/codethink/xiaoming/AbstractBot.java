package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.concurrent.Scheduler;
import cn.codethink.xiaoming.concurrent.SchedulerImpl;
import cn.codethink.xiaoming.configuration.BotConfiguration;
import cn.codethink.xiaoming.configuration.BotConfigurationImpl;
import cn.codethink.xiaoming.event.*;
import cn.codethink.xiaoming.exception.BotStopException;
import cn.codethink.xiaoming.logger.Logger;
import lombok.Data;

import java.io.File;

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
    protected BotConfiguration botConfiguration;
    
    @Override
    public void setBotConfiguration(BotConfiguration botConfiguration) {
        Preconditions.nonNull(botConfiguration, "core configuration");
        
        this.botConfiguration = botConfiguration;
        setupBotConfiguration(botConfiguration);
    }
    
    /**
     * 根据机器人设置调整底层机器人
     *
     * @param botConfiguration 机器人设置
     */
    protected abstract void setupBotConfiguration(BotConfiguration botConfiguration);
    
    /**
     * 核心调度器
     */
    protected Scheduler scheduler;
    
    /**
     * 日志记录器
     */
    protected Logger logger;
    
    /**
     * 事件管理器
     */
    protected EventManager eventManager;
    
    public AbstractBot() {
        this(new BotConfigurationImpl());
    }
    
    @SuppressWarnings("all")
    public AbstractBot(BotConfiguration botConfiguration) {
        Preconditions.nonNull(botConfiguration, "bot configuration");
        
        this.botConfiguration = botConfiguration;
        
//        // 关闭 jvm 时如果还没有关闭 Bot，则关闭
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            switch (state) {
//                case IDLE:
//                    break;
//                case STARTED:
//                case STOP_ERROR:
//                    stop();
//                    break;
//                case STOPPING:
//                case STARTING:
//                    break;
//                case FATAL_ERROR:
//                case START_ERROR:
//                default:
//                    throw new IllegalStateException();
//            }
//        }));
    }
    
    @Override
    public boolean isStopped() {
        return state == State.IDLE;
    }
    
    @Override
    public boolean isStarted() {
        return state == State.STARTED;
    }
    
    @Override
    public final boolean start() {
        switch (state) {
            case STARTING:
            case STOPPING:
                throw new IllegalStateException("can not change bot state in parallel");
            case IDLE:
            case START_ERROR:
                break;
            case STARTED:
            case STOP_ERROR:
            case FATAL_ERROR:
                return false;
            default:
                throw new IllegalStateException();
        }
    
        try {
            state = State.STARTING;
            
            // logger
            logger = botConfiguration.getLoggerFactory().getLogger("bot");
            logger.info("starting bot");
    
            // setup config
            setupBotConfiguration(botConfiguration);
    
            final File workingDirectory = botConfiguration.getWorkingDirectory();
            if (!workingDirectory.isDirectory() && !workingDirectory.mkdirs()) {
                logger.error("can not create working directory: " + workingDirectory.getAbsolutePath());

                state = State.STOP_ERROR;
                return false;
            }
            
            // 设置核心线程池
            scheduler = new SchedulerImpl(this, botConfiguration.getThreadCount());
        
            // 设置事件管理器
            eventManager = new EventManagerImpl(this);
        
            // 回调
            start0();
        
            state = State.STARTED;
        
            // 发出事件
            final BotStartEvent botStartEvent = new BotStartEventImpl(this);
            eventManager.broadcastEvent(botStartEvent);
        
            logger.info("bot started successfully");
            
            return true;
        } catch (Throwable throwable) {
            state = State.START_ERROR;
        
            throw new BotStopException(this, throwable);
        }
    }
    
    protected abstract void start0() throws Exception;
    
    @Override
    public final boolean stop() {
        switch (state) {
            case STARTING:
            case STOPPING:
                throw new IllegalStateException("can not change bot state in parallel");
            case STARTED:
            case STOP_ERROR:
                break;
            case IDLE:
            case START_ERROR:
            case FATAL_ERROR:
                return false;
            default:
                throw new IllegalStateException();
        }
        
        try {
            state = State.STOPPING;
        
            logger.info("stopping bot");
        
            // 发出事件
            final BotStopEvent botStopEvent = new BotStopEventImpl(this);
            eventManager.broadcastEvent(botStopEvent);
        
            // 关闭 Bot
            stop0();
        
            // 关闭线程池
            scheduler.shutdownGracefully();
            scheduler = null;
        
            // 关闭事件管理器
            eventManager = null;
        
            logger.info("bot stopped successfully");
        
            // 关闭日志记录器
            logger = null;
        
            state = State.IDLE;
            
            return true;
        } catch (Throwable throwable) {
            state = State.STOP_ERROR;
        
            throw new BotStopException(this, throwable);
        }
    }
    
    protected abstract void stop0() throws Exception;
    
    @Override
    public Bot getBot() {
        return this;
    }
}