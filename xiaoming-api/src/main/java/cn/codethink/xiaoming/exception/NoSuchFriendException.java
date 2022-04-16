package cn.codethink.xiaoming.exception;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

import java.util.NoSuchElementException;

/**
 * 和好友相关的异常
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class NoSuchFriendException
    extends NoSuchElementException
    implements BotObject {
    
    private final Code code;
    private final Bot bot;
    
    public NoSuchFriendException(Bot bot, Code code) {
        super("can not find the friend with code: " + code);
    
        Preconditions.nonNull(code, "code");
        Preconditions.nonNull(bot, "bot");
        
        this.code = code;
        this.bot = bot;
    }
}
