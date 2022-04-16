package cn.codethink.xiaoming.exception;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import lombok.Data;

/**
 * 机器人异常
 */
@Data
public class BotException
        extends Exception
        implements BotObject {
    
    protected final Bot bot;
    
    public BotException(Bot bot) {
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    public BotException(Bot bot, Throwable cause) {
        super(cause);
        
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    public BotException(Bot bot, String message) {
        super(message);
        
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    public BotException(Bot bot, String message, Throwable cause) {
        super(message, cause);
        
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
}