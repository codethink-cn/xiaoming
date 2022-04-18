package cn.codethink.xiaoming;

import cn.chuanwise.common.util.Preconditions;

import java.beans.Transient;

/**
 * 持有 bot 引用的对象
 *
 * @author Chuanwise
 */
public interface BotObject {
    
    /**
     * 获取该对象所属的 bot
     *
     * @return 该对象所属的 bot
     */
    @Transient
    Bot getBot();
    
    /**
     * 获取该对象所属的 bot
     *
     * @return 该对象所属的 bot
     * @throws java.util.NoSuchElementException bot 为 null 时
     */
    @Transient
    default Bot getBotOrFail() {
        final Bot bot = getBot();
        Preconditions.elementNonNull(bot, "no bot present");
        return bot;
    }
}
