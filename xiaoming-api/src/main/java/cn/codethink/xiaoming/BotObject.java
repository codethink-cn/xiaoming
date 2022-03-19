package cn.codethink.xiaoming;

import java.beans.Transient;

/**
 * 持有 Bot 引用的对象
 *
 * @author Chuanwise
 */
public interface BotObject {
    
    /**
     * 获取该对象所属的 Bot
     *
     * @return 该对象所属的 Bot
     */
    @Transient
    Bot getBot();
}
