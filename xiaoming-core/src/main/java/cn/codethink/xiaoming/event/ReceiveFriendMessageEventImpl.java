package cn.codethink.xiaoming.event;

import cn.codethink.xiaoming.Bot;
import cn.codethink.xiaoming.contact.Friend;
import cn.codethink.xiaoming.message.compound.CompoundMessage;

/**
 * @author Chuanwise
 *
 * @see ReceiveFriendMessageEvent
 */
public class ReceiveFriendMessageEventImpl
    extends AbstractOnlineMessageEvent
    implements ReceiveFriendMessageEvent {
    
    public ReceiveFriendMessageEventImpl(Friend sender, CompoundMessage message, long timestamp) {
        super(sender, message, sender.getBot(), timestamp);
    }
    
    @Override
    public Bot getTarget() {
        return (Bot) super.getTarget();
    }
    
    @Override
    public Friend getSender() {
        return (Friend) super.getSender();
    }
}
