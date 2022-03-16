package cn.codethink.xiaoming.exception;

import cn.codethink.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Scope;
import lombok.Data;

/**
 * 和好友相关的异常
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class NoSuchMemberException
        extends BotRuntimeException {
    
    private final Scope scope;
    
    private final Code code;
    
    public NoSuchMemberException(Bot bot, Scope scope, Code code) {
        super(bot, "can not find the member with code: " + code);
    
        Preconditions.namedArgumentNonNull(scope, "scope");
        Preconditions.namedArgumentNonNull(code, "code");
        
        this.scope = scope;
        this.code = code;
    }
}
