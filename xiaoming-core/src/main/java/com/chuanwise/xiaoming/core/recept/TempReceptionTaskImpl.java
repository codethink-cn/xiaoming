package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.TempMessage;
import com.chuanwise.xiaoming.api.recept.TempReceptionTask;
import com.chuanwise.xiaoming.api.user.TempXiaomingUser;
import com.chuanwise.xiaoming.core.account.AccountEventImpl;
import lombok.Getter;

import java.util.List;

@Getter
public class TempReceptionTaskImpl extends ReceptionTaskImpl implements TempReceptionTask {
    final TempXiaomingUser user;
    final List<TempMessage> recentMessages;

    protected TempReceptionTaskImpl(TempXiaomingUser user, List<TempMessage> recentMessages) {
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
        receptionist.getTempTasks().put(getUser().getContact().getGroupContact().getCodeString(), this);
    }

    @Override
    protected void unregister() {
        final GroupContact groupContact = user.getContact().getGroupContact();
        final String codeTag = groupContact.getCodeString();
        receptionist.getTempTasks().remove(codeTag);
    }

    @Override
    public void recept(Message message) throws Exception {
        if (getXiaomingBot().getInteractorManager().onInput(user, message)) {
            final AccountEventImpl event = new AccountEventImpl(message.serialize());
            final Account account = user.getOrPutAccount();

            account.addCommand(event);
            getXiaomingBot().getScheduler().readySave(account);

            getXiaomingBot().getUserCallLimitManager().getGroupCallLimiter().addCallRecord(user.getCode());
        } else {
            user.getRecentMessages().clear();
        }
    }
}
