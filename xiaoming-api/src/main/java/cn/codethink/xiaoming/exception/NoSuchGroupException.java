package cn.codethink.xiaoming.exception;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

/**
 * 和好友相关的异常
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class NoSuchGroupException
        extends BotRuntimeException {
    
    private final Code code;
    
    public NoSuchGroupException(Bot bot, Code code) {
        super(bot, "can not find the group with code: " + code);
    
        Preconditions.namedArgumentNonNull(code, "code");
        
        this.code = code;
    }
}
