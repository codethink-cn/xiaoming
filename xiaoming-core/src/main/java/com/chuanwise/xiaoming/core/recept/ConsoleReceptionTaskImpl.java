package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.message.ConsoleMessage;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.recept.ConsoleReceptionTask;
import com.chuanwise.xiaoming.api.user.ConsoleXiaomingUser;
import com.chuanwise.xiaoming.api.user.PrivateXiaomingUser;
import com.chuanwise.xiaoming.core.account.AccountEventImpl;
import lombok.Getter;

import java.util.List;

@Getter
public class ConsoleReceptionTaskImpl extends ReceptionTaskImpl implements ConsoleReceptionTask {
    final ConsoleXiaomingUser user;
    final List<ConsoleMessage> recentMessages;

    protected ConsoleReceptionTaskImpl(ConsoleXiaomingUser user, List<ConsoleMessage> recentMessages) {
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
    }

    @Override
    protected void unregister() {
        receptionist.setPrivateTask(null);
    }

    @Override
    public void recept(Message message) throws Exception {
        if (getXiaomingBot().getInteractorManager().onInput(user, message)) {
            final AccountEventImpl event = new AccountEventImpl(message.serialize());
            final Account account = user.getOrPutAccount();

            account.addCommand(event);
            getXiaomingBot().getFinalizer().readySave(account);

            getXiaomingBot().getUserCallLimitManager().getGroupCallLimiter().addCallRecord(user.getCode());
        } else {
            user.getRecentMessages().clear();
        }
    }
}
