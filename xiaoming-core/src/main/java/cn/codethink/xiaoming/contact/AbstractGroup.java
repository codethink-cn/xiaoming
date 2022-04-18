package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.Bot;

/**
 * @author Chuanwise
 *
 * @see Group
 * @see cn.codethink.xiaoming.contact.Mass
 */
public abstract class AbstractGroup
    extends AbstractContact
    implements Group {
    
    public AbstractGroup(Bot bot) {
        super(bot);
    }
}
