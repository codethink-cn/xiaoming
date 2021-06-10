package com.chuanwise.xiaoming.core.contact.message;

import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public class GroupMessageImpl extends MessageImpl implements GroupMessage {
    final GroupContact contact;
    final GroupXiaomingUser user;

    public GroupMessageImpl(GroupXiaomingUser user, MessageChain messages) {
        super(user.getXiaomingBot(), messages);
        this.contact = user.getContact();
        this.user = user;
    }

    @Override
    public GroupContact getContact() {
        return contact;
    }

    @Override
    public GroupXiaomingUser getSender() {
        return user;
    }
}
