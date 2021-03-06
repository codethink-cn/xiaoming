package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;

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
        Preconditions.nonNull(bot, "bot");
        
        this.bot = bot;
    }
    
    @Override
    @Transient
    public final Bot getBot() {
        return bot;
    }
}
