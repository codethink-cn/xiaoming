package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

@Getter
public class ConsoleMessageImpl extends MessageImpl implements ConsoleMessage {
    final ConsoleXiaomingUser sender;
    final ConsoleContact contact;

    public ConsoleMessageImpl(ConsoleXiaomingUser sender, MessageChain messages) {
        super(sender.getXiaomingBot(), messages);
        this.sender = sender;
        this.contact = sender.getContact();
    }

    @Override
    public ConsoleMessage clone() throws CloneNotSupportedException {
        return ((ConsoleMessage) super.clone());
    }
}