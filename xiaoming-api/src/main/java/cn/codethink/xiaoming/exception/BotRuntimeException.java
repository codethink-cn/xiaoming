package cn.codethink.xiaoming.exception;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import lombok.Data;

/**
 * 机器人运行时异常
 *
 * @author Chuanwise
 */
@Data
public class BotRuntimeException
        extends RuntimeException
        implements BotObject {
    
    protected final Bot bot;
    
    public BotRuntimeException(Bot bot) {
        Preconditions.namedArgumentNonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    public BotRuntimeException(Bot bot, Throwable cause) {
        super(cause);
        
        Preconditions.namedArgumentNonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    public BotRuntimeException(Bot bot, String message) {
        super(message);
        
        Preconditions.namedArgumentNonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    public BotRuntimeException(Bot bot, String message, Throwable cause) {
        super(message, cause);
        
        Preconditions.namedArgumentNonNull(bot, "bot");
        
        this.bot = bot;
    }
}