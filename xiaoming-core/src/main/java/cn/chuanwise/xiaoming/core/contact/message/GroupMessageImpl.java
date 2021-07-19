package cn.chuanwise.xiaoming.core.contact.message;

import cn.chuanwise.xiaoming.api.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.api.contact.message.GroupMessage;
import cn.chuanwise.xiaoming.api.user.GroupXiaomingUser;
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

    @Override
    public GroupMessage clone() throws CloneNotSupportedException {
        return ((GroupMessage) super.clone());
    }
}
