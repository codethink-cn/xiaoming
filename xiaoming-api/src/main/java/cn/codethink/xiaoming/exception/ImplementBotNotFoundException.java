package cn.codethink.xiaoming.exception;

import cn.codethink.xiaoming.Bot;

/**
 * 缺少必要的 Bot 异常
 *
 * @author Chuanwise
 */
public class ImplementBotNotFoundException
    extends BotRuntimeException {
    
    public ImplementBotNotFoundException(Bot bot) {
        super(bot);
    }
}
