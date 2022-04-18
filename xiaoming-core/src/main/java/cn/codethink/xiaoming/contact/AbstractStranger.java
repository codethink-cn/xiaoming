package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.Bot;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.contact.Stranger
 */
public abstract class AbstractStranger
    extends AbstractContact
    implements Stranger {
    
    public AbstractStranger(Bot bot) {
        super(bot);
    }
    
    @Override
    public Stranger asStranger() {
        return this;
    }
    
    @Override
    public Friend asFriend() {
        return bot.getFriend(getCode());
    }
}
