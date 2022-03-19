package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;

/**
 * @see Friend
 * @author Chuanwise
 */
public abstract class AbstractFriend
    extends AbstractBotObject
    implements Friend {
    
    public AbstractFriend(Bot bot) {
        super(bot);
    }
}
