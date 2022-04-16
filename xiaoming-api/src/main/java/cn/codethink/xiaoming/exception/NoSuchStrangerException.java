package cn.codethink.xiaoming.exception;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

import java.util.NoSuchElementException;

/**
 * 和陌生人相关的异常
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class NoSuchStrangerException
    extends NoSuchElementException
    implements BotObject {
    
    private final Code code;
    
    private final Bot bot;
    
    public NoSuchStrangerException(Bot bot, Code code) {
        super("can not find the stranger with code: " + code);
    
        Preconditions.nonNull(code, "code");
        Preconditions.nonNull(bot, "bot");
        
        this.code = code;
        this.bot = bot;
    }
}
