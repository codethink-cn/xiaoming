package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.Bot;
import lombok.Getter;

/**
 * @see Friend
 * @author Chuanwise
 */
@Getter
public abstract class AbstractFriend
    extends AbstractContact
    implements Friend {
    
    public AbstractFriend(Bot bot) {
        super(bot);
    }
    
    @Override
    public Friend asFriend() {
        return this;
    }
    
    @Override
    public Stranger asStranger() {
        return bot.getStranger(getCode());
    }
}
