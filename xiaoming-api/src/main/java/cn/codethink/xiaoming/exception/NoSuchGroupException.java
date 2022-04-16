package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.code.Code;
import lombok.Data;

/**
 * 找不到群聊时抛出的异常
 *
 * @author Chuanwise
 */
@Data
@SuppressWarnings("all")
public class NoSuchGroupException
    extends NoSuchMassException {
    
    public NoSuchGroupException(Bot bot, Code code) {
        super(bot, code);
    }
}
