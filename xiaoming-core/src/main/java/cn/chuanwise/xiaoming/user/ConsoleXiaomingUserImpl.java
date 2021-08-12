package cn.chuanwise.xiaoming.user;

import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.recept.ConsoleReceptionTask;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessageImpl;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Chuanwise
 */
@Data
public class ConsoleXiaomingUserImpl extends XiaomingUserImpl<ConsoleContact, ConsoleMessage, ConsoleReceptionTask> implements ConsoleXiaomingUser {
    long code;
    final ConsoleContact contact;
    final List<ConsoleMessage> recentMessages;
    ConsoleReceptionTask receptionTask;

    public ConsoleXiaomingUserImpl(ConsoleContact contact) {
        super(contact.getXiaomingBot(), 0);
        this.contact = contact;
        this.recentMessages = new SizedCopyOnWriteArrayList<>(getXiaomingBot().getConfiguration().getMaxRecentMessageBufferSize());
    }

    public void setCode(long code) {
        this.code = code;
    }

    @Override
    public String getCompleteName() {
        return "后台";
    }

    @Override
    public void onNextInput(MessageChain messages) {
        onNextInput(new ConsoleMessageImpl(this, messages));
    }

    @Override
    public void onNextInput(ConsoleMessage message) {
        final List<ConsoleMessage> list = getRecentMessages();
        setProperty(PropertyType.LAST, message);
        list.add(message);

        final Receptionist receptionist = getReceptionist();
        receptionist.setGlobalRecentMessages(list);

        synchronized (list) {
            list.notifyAll();
        }
        synchronized (receptionist) {
            receptionist.notifyAll();
        }
    }

    @Override
    public ConsoleMessage sendPrivateMessage(String message, Object... arguments) {
        sendMessage(message, arguments);
        final MessageChain messages = MiraiCode.deserializeMiraiCode(replaceArguments(message, arguments));
        return new ConsoleMessageImpl(this, messages);
    }
}
