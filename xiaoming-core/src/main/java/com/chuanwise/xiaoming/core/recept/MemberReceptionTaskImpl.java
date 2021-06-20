package com.chuanwise.xiaoming.core.recept;

import com.chuanwise.xiaoming.api.account.Account;
import com.chuanwise.xiaoming.api.account.record.CommandRecord;
import com.chuanwise.xiaoming.api.account.record.MemberCommandRecord;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.contact.message.MemberMessage;
import com.chuanwise.xiaoming.api.recept.MemberReceptionTask;
import com.chuanwise.xiaoming.api.user.MemberXiaomingUser;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberReceptionTaskImpl extends ReceptionTaskImpl implements MemberReceptionTask {
    final MemberXiaomingUser user;
    final List<MemberMessage> recentMessages;

    protected MemberReceptionTaskImpl(MemberXiaomingUser user, List<MemberMessage> recentMessages) {
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
        receptionist.getMemberTasks().put(getUser().getContact().getGroupContact().getCodeString(), this);
    }

    @Override
    protected void unregister() {
        final GroupContact groupContact = user.getContact().getGroupContact();
        final String codeTag = groupContact.getCodeString();
        receptionist.getMemberTasks().remove(codeTag);
    }

    @Override
    public void recept(Message message) throws Exception {
        getXiaomingBot().getInteractorManager().onInput(user, message);
    }
}
