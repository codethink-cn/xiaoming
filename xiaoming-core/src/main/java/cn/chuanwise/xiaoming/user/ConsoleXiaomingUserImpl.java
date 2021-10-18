package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Optional;

/**
 * @author Chuanwise
 */
@Getter
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl<ConsoleContact> implements ConsoleXiaomingUser {
    @Setter
    long code;

    final ConsoleContact contact;

    public ConsoleXiaomingUserImpl(ConsoleContact contact) {
        super(contact.getXiaomingBot(), 0);
        this.contact = contact;
    }

    public void setCode(long code) {
        this.code = code;
    }

    @Override
    public String getCompleteName() {
        return "后台";
    }

    @Override
    public Optional<Message> sendPrivateMessage(String message, Object... arguments) {
        sendMessage(message, arguments);
        final MessageChain messages = MiraiCode.deserializeMiraiCode(format(message, arguments));
        return Optional.of(new MessageImpl(xiaomingBot, messages));
    }
}
