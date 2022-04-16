package cn.codethink.xiaoming.exception;

import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.BotObject;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Mass;
import lombok.Data;

import java.util.NoSuchElementException;

/**
 * 和好友相关的异常
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class NoSuchMemberException
    extends NoSuchElementException
    implements BotObject {
    
    private final Mass mass;
    
    private final Code code;
    
    public NoSuchMemberException(Mass mass, Code code) {
        super("can not find the member with code: " + code);
    
        Preconditions.nonNull(code, "code");
        
        this.mass = mass;
        this.code = code;
    }
    
    @Override
    public Bot getBot() {
        return mass.getBot();
    }
}
