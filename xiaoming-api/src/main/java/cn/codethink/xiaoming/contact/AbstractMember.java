package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

/**
 * @see Friend
 * @author Chuanwise
 */
public abstract class AbstractMember
    extends AbstractBotObject
    implements Member {
    
    public AbstractMember(Bot bot) {
        super(bot);
    }
}
