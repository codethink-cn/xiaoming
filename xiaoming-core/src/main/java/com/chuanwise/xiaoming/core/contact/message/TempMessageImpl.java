package com.chuanwise.xiaoming.core.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.TempContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public class TempMessageImpl extends MessageImpl implements TempMessage {
    final TempContact contact;
    final TempXiaomingUser user;

    public TempMessageImpl(TempXiaomingUser user, MessageChain messages) {
        super(user.getXiaomingBot(), messages);
        this.contact = user.getContact();
        this.user = user;
    }

    @Override
    public TempXiaomingUser getSender() {
        return user;
    }

    @Override
    public TempMessage clone() throws CloneNotSupportedException {
        return ((TempMessage) super.clone());
    }
}
