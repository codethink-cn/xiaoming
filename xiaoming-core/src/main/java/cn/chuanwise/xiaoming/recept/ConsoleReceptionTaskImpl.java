package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.account.record.CommandRecord;
import cn.chuanwise.xiaoming.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.user.ConsoleXiaomingUser;
import lombok.Getter;

import java.util.List;

@Getter
public class ConsoleReceptionTaskImpl extends ReceptionTaskImpl implements ConsoleReceptionTask {
    final ConsoleXiaomingUser user;
    final List<ConsoleMessage> recentMessages;

    protected ConsoleReceptionTaskImpl(ConsoleXiaomingUser user, ConsoleMessage message) {
        super(user.getReceptionist(), "reception-task[console]", message);
        this.user = user;
        user.setReceptionTask(this);
        this.recentMessages = user.getRecentMessages();
    }

    @Override
    protected void register() {
        thread = Thread.currentThread();
        thread.setName(identify);
    }

    @Override
    protected void unregister() {
        receptionist.setPrivateTask(null);
    }
}
