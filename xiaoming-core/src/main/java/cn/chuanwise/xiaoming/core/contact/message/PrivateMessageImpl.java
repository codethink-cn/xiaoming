package cn.chuanwise.xiaoming.core.contact.message;

import cn.chuanwise.xiaoming.api.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.api.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public class PrivateMessageImpl extends MessageImpl implements PrivateMessage {
    final PrivateXiaomingUser user;

    public PrivateMessageImpl(PrivateXiaomingUser user, MessageChain messages) {
        super(user.getContact().getXiaomingBot(), messages);
        this.user = user;
    }

    @Override
    public PrivateContact getContact() {
        return user.getContact();
    }

    @Override
    public PrivateXiaomingUser getSender() {
        return user;
    }

    @Override
    public PrivateMessage clone() throws CloneNotSupportedException {
        return ((PrivateMessage) super.clone());
    }
}
