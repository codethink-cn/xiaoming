package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

/**
 * @see Group
 * @author Chuanwise
 */
public abstract class AbstractGroup
        extends AbstractBotObject
        implements Group {
    
    public AbstractGroup(Bot bot) {
        super(bot);
    }
}
