package cn.chuanwise.xiaoming.core.recept;

import cn.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import cn.chuanwise.xiaoming.api.recept.ConsoleReceptionTask;
import cn.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
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
