package cn.codethink.xiaoming;

import cn.codethink.util.Preconditions;

import java.beans.Transient;

/**
 * 自带实现的 Bot 所属对象
 *
 * @author Chuanwise
 */
public abstract class AbstractBotObject
        implements BotObject {
    
    protected transient final Bot bot;
    
    public AbstractBotObject(Bot bot) {
        Preconditions.namedArgumentNonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    @Override
    @Transient
    public final Bot getBot() {
        return bot;
    }
}
