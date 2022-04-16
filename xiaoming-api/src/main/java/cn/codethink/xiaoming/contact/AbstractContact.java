package cn.codethink.xiaoming.contact;

import cn.codethink.xiaoming.AbstractBotObject;
import cn.codethink.xiaoming.Bot;
import lombok.Getter;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.contact.Contact
 * @see cn.codethink.xiaoming.contact.ContactOrBot
 */
@Getter
public abstract class AbstractContact
    extends AbstractBotObject
    implements Contact {
    
    /**
     * 会话是否仍然有效
     */
    protected volatile boolean available = true;
    
    public AbstractContact(Bot bot) {
        super(bot);
    }
}
