package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.contact.message.PrivateMessage;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import lombok.Getter;

import java.util.List;

@Getter
public class PrivateReceptionTaskImpl extends ReceptionTaskImpl implements PrivateReceptionTask {
    final PrivateXiaomingUser user;
    final List<PrivateMessage> recentMessages;

    protected PrivateReceptionTaskImpl(PrivateXiaomingUser user, PrivateMessage message) {
        super(user.getReceptionist(), "reception-task[" + user.getCompleteName() + "]", message);
        this.user = user;
        user.setReceptionTask(this);
        this.recentMessages = getXiaomingBot().getContactManager().forPrivateMessages(user.getCodeString());
    }

    @Override
    protected void register() {
        thread = Thread.currentThread();
        thread.setName(identify);
        receptionist.setPrivateTask(this);
    }

    @Override
    protected void unregister() {
        receptionist.setPrivateTask(null);
    }
}
