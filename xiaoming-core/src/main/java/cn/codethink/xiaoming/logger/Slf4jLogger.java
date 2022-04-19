package cn.codethink.xiaoming.logger;

import cn.codethink.common.util.Preconditions;
import lombok.Data;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Slf4j 日志
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class Slf4jLogger
    extends AbstractLogger {
    
    private final org.slf4j.Logger logger;
    
    public Slf4jLogger(org.slf4j.Logger logger) {
        super(logger.getName());
        
        this.logger = logger;
    }
    
    @Override
    public void log(LoggerLevel level, Object message, Object... arguments) {
        Preconditions.nonNull(level, "level");
        Preconditions.nonNull(message, "message");
        Preconditions.nonNull(arguments, "arguments");
    
        final String finalMessage = Objects.toString(message);
    
        switch (level) {
            case INFO:
                logger.info(finalMessage, arguments);
                break;
            case WARN:
                logger.warn(finalMessage, arguments);
                break;
            case DEBUG:
                logger.debug(finalMessage, arguments);
                break;
            case ERROR:
                logger.error(finalMessage, arguments);
                break;
            case FATAL:
                logger.error(finalMessage, arguments);
                break;
            case TRACE:
                logger.trace(finalMessage, arguments);
                break;
            default:
                throw new NoSuchElementException();
        }
    }
}
