package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.recept.GroupReceptionTask;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.core.account.AccountEventImpl;
import lombok.Getter;

import java.util.List;

@Getter
public class GroupReceptionTaskImpl extends ReceptionTaskImpl implements GroupReceptionTask {
    final GroupXiaomingUser user;
    final List<GroupMessage> recentMessages;

    public GroupReceptionTaskImpl(GroupXiaomingUser user, List<GroupMessage> recentMessages) {
        super(user.getReceptionist(), "reception-task[" + user.getCompleteName() + "]");
        this.user = user;
        user.setReceptionTask(this);
        this.recentMessages = recentMessages;
    }

    @Override
    public void stop() {
        busy = false;
        running = false;

        if (thread.isAlive()) {
            thread.interrupt();
        }

        unregister();
    }

    @Override
    protected void register() {
        thread = Thread.currentThread();
        thread.setName(identify);
        receptionist.getGroupTasks().put(getUser().getGroupCodeString(), this);
    }

    @Override
    protected void unregister() {
        receptionist.getGroupTasks().remove(user.getGroupCodeString());
    }

    @Override
    public void recept(Message message) throws Exception {
        if (getXiaomingBot().getInteractorManager().onInput(user, message)) {
            final AccountEventImpl event = new AccountEventImpl(user.getGroupCode(), message.serialize());

            final Account account = user.getOrPutAccount();
            account.addCommand(event);
            getXiaomingBot().getScheduler().readySave(account);

            getXiaomingBot().getUserCallLimitManager().getGroupCallLimiter().addCallRecord(user.getCode());
        } else {
            user.getRecentMessages().clear();
        }
    }
}
