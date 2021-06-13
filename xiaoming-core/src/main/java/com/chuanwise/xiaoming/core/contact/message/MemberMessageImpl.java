package com.chuanwise.xiaoming.core.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.MemberContact;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public class MemberMessageImpl extends MessageImpl implements MemberMessage {
    final MemberContact contact;
    final MemberXiaomingUser user;

    public MemberMessageImpl(MemberXiaomingUser user, MessageChain messages) {
        super(user.getXiaomingBot(), messages);
        this.contact = user.getContact();
        this.user = user;
    }

    @Override
    public MemberXiaomingUser getSender() {
        return user;
    }

    @Override
    public MemberMessage clone() throws CloneNotSupportedException {
        return ((MemberMessage) super.clone());
    }
}
